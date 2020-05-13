package pl.lukasz.culer.utils

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pl.lukasz.culer.annotations.Exclude


class JsonController {
    companion object {
        //inner objects
        private val exclusionStrategy: ExclusionStrategy = object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                return false
            }

            override fun shouldSkipField(field: FieldAttributes): Boolean {
                return field.getAnnotation(Exclude::class.java) != null
            }
        }

        //proper serializer / deserializer
        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .setExclusionStrategies(exclusionStrategy)
            .create()
    }
}