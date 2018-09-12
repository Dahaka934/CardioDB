package ru.dahaka934.cardiodb.view.internal

import javafx.scene.control.TableCell
import ru.dahaka934.cardiodb.util.LocalDateConverter
import java.time.LocalDate

class TableDateCell<T> : TableCell<T, LocalDate>() {
    override fun updateItem(item: LocalDate?, empty: Boolean) {
        super.updateItem(item, empty)
        text = if (item != null && !empty) item.format(LocalDateConverter.formatter) else null
    }
}