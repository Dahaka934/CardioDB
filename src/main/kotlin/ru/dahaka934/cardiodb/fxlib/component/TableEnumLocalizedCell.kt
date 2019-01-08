package ru.dahaka934.cardiodb.fxlib.component

import javafx.scene.control.TableCell
import ru.dahaka934.cardiodb.fxlib.internal.LocalizedObj

class TableEnumLocalizedCell<T, K : LocalizedObj> : TableCell<T, K>() {
    override fun updateItem(item: K?, empty: Boolean) {
        super.updateItem(item, empty)
        text = if (item != null && !empty) item.localName else null
    }
}