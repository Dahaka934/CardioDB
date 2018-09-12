package ru.dahaka934.cardiodb.util

import javafx.util.StringConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateISOConverter : StringConverter<LocalDate?>() {
    override fun toString(obj: LocalDate?): String? {
        return tryWithError("Не удалось форматировать дату") {
            obj?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        } ?: ""
    }

    override fun fromString(string: String?): LocalDate? {
        return tryWithError("Не удалось форматировать дату") {
            if (string != null && string.isNotEmpty() && string != "null") {
                LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE)
            } else null
        }
    }
}