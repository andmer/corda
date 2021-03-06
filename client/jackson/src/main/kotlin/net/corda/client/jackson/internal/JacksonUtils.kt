package net.corda.client.jackson.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.module.SimpleModule

inline fun <reified T : Any> SimpleModule.addSerAndDeser(serializer: JsonSerializer<in T>, deserializer: JsonDeserializer<T>) {
    addSerializer(T::class.java, serializer)
    addDeserializer(T::class.java, deserializer)
}

inline fun JsonGenerator.jsonObject(fieldName: String? = null, gen: JsonGenerator.() -> Unit) {
    fieldName?.let { writeFieldName(it) }
    writeStartObject()
    gen()
    writeEndObject()
}

inline fun <reified T> JsonParser.readValueAs(): T = readValueAs(T::class.java)
