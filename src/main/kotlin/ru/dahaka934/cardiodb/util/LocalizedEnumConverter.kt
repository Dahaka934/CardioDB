package ru.dahaka934.cardiodb.util

import javafx.util.StringConverter
import ru.dahaka934.cardiodb.data.LocalizedObj

class LocalizedEnumConverter<T>(val klass: Class<T>) : StringConverter<T>()
    where T : Enum<T>, T : LocalizedObj {

    override fun fromString(string: String?): T? {
        string ?: return null
        return klass.enumConstants.firstOrNull { it.localName == string }
    }

    override fun toString(obj: T?): String? = obj?.localName
}