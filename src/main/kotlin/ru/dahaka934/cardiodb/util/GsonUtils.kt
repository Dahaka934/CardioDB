package ru.dahaka934.cardiodb.util

import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.hildan.fxgson.FxGson
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.time.LocalDate

object GsonHelper {
    val GSON = FxGson.fullBuilder()
        .registerTypeAdapter(LocalDate::class.java, GsonTypeAdapterLocalDate)
        .setPrettyPrinting()
        .create()

    fun <T> writeObj(obj: T): String? = tryWithIgnore { GSON.toJson(obj) }

    fun <T> readObj(type: Type, obj: String): T? = tryWithIgnore {
        return if (obj.isNotEmpty()) GSON.fromJson(obj, type) else null
    }

    fun <T> readObj(type: Type, file: File): T? = readObj(type, IOTools.readString(file))

    inline fun <reified T> readObj(obj: String): T? = readObj(type<T>(), obj)

    inline fun <reified T> readObj(file: File): T? = readObj(type<T>(), file)

    inline fun <reified T> readObj(obj: String, def: () -> T): T = readObj(type<T>(), obj) ?: def()

    inline fun <reified T> readObj(file: File, def: () -> T): T = readObj(type<T>(), file) ?: def()

    inline fun <reified T> readObjOrRewrite(file: File, def: () -> T): T {
        return readObj(file) ?: def().also {
            val str = writeObj(it)
            if (str != null) {
                IOTools.writeString(file, str, true)
            }
        }
    }
}

inline fun <reified T> GsonBuilder.registerInstanceCreator(crossinline factory: () -> T): GsonBuilder = apply {
    registerTypeAdapter(type<T>(), InstanceCreator { factory() })
}

inline fun <reified T> type() = object : TypeToken<T>() {}.type

object GsonTypeAdapterLocalDate : TypeAdapter<LocalDate?>() {

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: LocalDate?) {
        val str = LocalDateISOConverter.toString(value)

        if (str != null) {
            writer.value(str)
        } else {
            writer.value("null")
        }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): LocalDate? {
        return LocalDateISOConverter.fromString(reader.nextString())
    }
}
