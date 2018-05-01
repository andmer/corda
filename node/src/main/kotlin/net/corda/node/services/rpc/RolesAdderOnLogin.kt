package net.corda.node.services.rpc

import org.apache.activemq.artemis.core.security.Role
import org.apache.activemq.artemis.core.server.SecuritySettingPlugin
import org.apache.activemq.artemis.core.settings.HierarchicalRepository

/**
 * Helper class to dynamically assign security roles to RPC users
 * on their authentication. This object is plugged into the server
 * as [SecuritySettingPlugin]. It responds to authentication events
 * from [BrokerJaasLoginModule] by adding the address -> roles association
 * generated by the given [source], unless already done before.
 */
internal class RolesAdderOnLogin(val systemUsers: List<String> = emptyList(), val source: (String) -> Pair<String, Set<Role>>) : SecuritySettingPlugin {
    private lateinit var repository: RolesRepository

    fun onLogin(username: String) {
        val (address, roles) = source(username)
        val entry = repository.getMatch(address)
        if (entry == null || entry.isEmpty()) {
            repository.addMatch(address, roles.toMutableSet())
        }
    }

    override fun setSecurityRepository(repository: RolesRepository) {
        this.repository = repository
        systemUsers.forEach(::onLogin)
    }

    override fun stop() = this

    override fun init(options: MutableMap<String, String>?) = this

    override fun getSecurityRoles() = null
}

typealias RolesRepository = HierarchicalRepository<MutableSet<Role>>