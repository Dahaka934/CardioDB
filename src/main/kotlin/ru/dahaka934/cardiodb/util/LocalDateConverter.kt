package ru.dahaka934.cardiodb.util

import javafx.util.StringConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateConverter : StringConverter<LocalDate?>() {
    val formatter: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override fun toString(obj: LocalDate?): String {
        return obj?.format(formatter) ?: ""
    }

    override fun fromString(string: String?): LocalDate? {
        // Что-то идет не так и не конвертится
        // Поэтому есть костыль - другая реализация : LocalDateISOConverter
        throw UnsupportedOperationException()
    }
}