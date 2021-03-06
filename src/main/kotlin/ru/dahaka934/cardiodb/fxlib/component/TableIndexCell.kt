package ru.dahaka934.cardiodb.fxlib.component

import javafx.scene.control.TableCell

class TableIndexCell : TableCell<Any, Int>() {
    override fun updateItem(item: Int?, empty: Boolean) {
        super.updateItem(item, empty)
        text = (if (empty) "" else (index + 1).toString())
    }
}