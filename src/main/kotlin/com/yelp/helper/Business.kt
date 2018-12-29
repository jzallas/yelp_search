package com.yelp.helper

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

@JsonAdapter(Business.Deserializer::class)
data class Business(
        val name: String,
        val url: String,
        val city: String,
        val displayAddress: String,
        val isClosed: Boolean,
        val zipCode: String,
        val phone: String
) {
    companion object {
        fun headers() = Business::class.java
                .declaredFields
                .map { it.name }
                .toTypedArray()
    }

    fun values() = Business::class.java
            .declaredFields
            .map { it.get(this) }
            .map { it.toString() }
            .toTypedArray()

    class Deserializer : JsonDeserializer<Business> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Business {
            return json.asJsonObject.run {
                Business(
                        name = get("name").asString,
                        url = get("url").asString,
                        city = get("location").asJsonObject
                                .get("city").asString,
                        displayAddress = get("location").asJsonObject
                                .get("display_address").asJsonArray
                                .joinToString(" "),
                        isClosed = get("is_closed").asBoolean,
                        zipCode = get("location").asJsonObject
                                .get("zip_code").asString,
                        phone = get("phone").asString
                )
            }
        }
    }
}
