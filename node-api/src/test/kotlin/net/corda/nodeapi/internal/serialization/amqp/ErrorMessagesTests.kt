package net.corda.nodeapi.internal.serialization.amqp

import net.corda.nodeapi.internal.serialization.amqp.testutils.TestSerializationOutput
import net.corda.nodeapi.internal.serialization.amqp.testutils.deserialize
import net.corda.nodeapi.internal.serialization.amqp.testutils.serialize
import net.corda.nodeapi.internal.serialization.amqp.testutils.testDefaultFactory
import net.corda.nodeapi.internal.serialization.amqp.testutils.testName
import org.assertj.core.api.Assertions
import org.junit.Ignore
import org.junit.Test
import java.io.NotSerializableException

class ErrorMessagesTests {
    companion object {
        val VERBOSE get() = false
    }

    private fun errMsg(property: String, testname: String) =
            "Property '$property' or its getter is non public, this renders class 'class $testname\$C' unserializable -> class $testname\$C"

    // Java allows this to be set at the class level yet Kotlin doesn't for some reason
    @Ignore("Current behaviour allows for the serialization of objects with private members, this will be disallowed at some point in the future")
    @Test
    fun privateProperty() {
        data class C(private val a: Int)

        val sf = testDefaultFactory()

        val testname = "${javaClass.name}\$${testName()}"

        Assertions.assertThatThrownBy {
            TestSerializationOutput(VERBOSE, sf).serialize(C(1))
        }.isInstanceOf(NotSerializableException::class.java).hasMessage(errMsg("a", testname))
    }

    // Java allows this to be set at the class level yet Kotlin doesn't for some reason
    @Ignore("Current behaviour allows for the serialization of objects with private members, this will be disallowed at some point in the future")
    @Test
    fun privateProperty2() {
        data class C(val a: Int, private val b: Int)

        val sf = testDefaultFactory()

        val testname = "${javaClass.name}\$${testName()}"

        Assertions.assertThatThrownBy {
            TestSerializationOutput(VERBOSE, sf).serialize(C(1, 2))
        }.isInstanceOf(NotSerializableException::class.java).hasMessage(errMsg("b", testname))
    }

    // Java allows this to be set at the class level yet Kotlin doesn't for some reason
    @Ignore("Current behaviour allows for the serialization of objects with private members, this will be disallowed at some point in the future")
    @Test
    fun privateProperty3() {
        // despite b being private, the getter we've added is public and thus allows for the serialisation
        // of the object
        data class C(val a: Int, private val b: Int) {
            public fun getB() = b
        }

        val sf = testDefaultFactory()

        val testname = "${javaClass.name}\$${testName()}"

        val bytes = TestSerializationOutput(VERBOSE, sf).serialize(C(1, 2))
        val c = DeserializationInput(sf).deserialize(bytes)
    }

    // Java allows this to be set at the class level yet Kotlin doesn't for some reason
    @Ignore("Current behaviour allows for the serialization of objects with private members, this will be disallowed at some point in the future")
    @Test
    fun protectedProperty() {
        data class C(protected val a: Int)

        val sf = testDefaultFactory()

        val testname = "${javaClass.name}\$${testName()}"

        Assertions.assertThatThrownBy {
            TestSerializationOutput(VERBOSE, sf).serialize(C(1))
        }.isInstanceOf(NotSerializableException::class.java).hasMessage(errMsg("a", testname))
    }
}