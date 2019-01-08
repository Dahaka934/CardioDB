package ru.dahaka934.cardiodb.fxlib.component

import javafx.scene.control.TableCell
import ru.dahaka934.cardiodb.fxlib.internal.LocalDateConverter
import java.time.LocalDate

class TableDateCell<T> : TableCell<T, LocalDate>() {
    override fun updateItem(item: LocalDate?, empty: Boolean) {
        super.updateItem(item, empty)
        text = if (item != null && !empty) LocalDateConverter.toString(item) else null
    }
}