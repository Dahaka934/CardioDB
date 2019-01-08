package ru.dahaka934.cardiodb.data

import ru.dahaka934.cardiodb.AppProperties

class User {
    var name: String = ""

    var place: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                oldPlaces.add(value)
            }
            field = value
        }

    val oldPlaces = HashSet<String>()

    val isValid: Boolean
        get() = name.isNotEmpty() && place.isNotEmpty()

    fun load() {
        oldPlaces.clear()
        oldPlaces += AppProperties.getStringList("user_places")

        name = AppProperties.getProperty("user_name") ?: ""
        place = AppProperties.getProperty("user_place") ?: ""
    }

    fun save() {
        AppProperties.setProperty("user_name", name)
        AppProperties.setProperty("user_place", place)
        AppProperties.setStringList("user_places", oldPlaces)
    }

    override fun toString(): String {
        return if (isValid) "$place     $name" else "???"
    }
}