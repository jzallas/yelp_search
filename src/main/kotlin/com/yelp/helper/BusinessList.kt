package com.yelp.helper

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

@JsonAdapter(BusinessList.Deserializer::class)
data class BusinessList(
        val list: List<Business>
) : Iterable<Business> by list {
    class Deserializer : JsonDeserializer<BusinessList> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BusinessList {
            val businesses = json.asJsonObject
                    .get("businesses")
                    .asJsonArray
                    .map { context.deserialize<Business>(it, Business::class.java) }

            return BusinessList(businesses)
        }
    }
}

