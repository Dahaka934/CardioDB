package ru.dahaka934.cardiodb.view.internal

import javafx.application.Platform
import javafx.event.Event
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode.*
import javafx.scene.input.KeyEvent

object FXHelper {
    fun bindTabOnPressEnter(node: Node) {
        node.setOnKeyPressed {
            if (it.code == ENTER) {
                val newEvent = KeyEvent(null, null, KeyEvent.KEY_PRESSED, "", "\t", TAB, it.isShiftDown,
                                        it.isControlDown, it.isAltDown, it.isMetaDown)
                Event.fireEvent(it.target, newEvent)
                it.consume()
            }
        }
    }

    fun bindClickOnPressEnter(button: Button) {
        button.setOnKeyPressed {
            if (it.code == ENTER) {
                button.fire()
            }
        }
    }
}

fun <T> TableView<T>.select(obj: T) {
    selectionModel.select(obj)
    Platform.runLater {
        scrollTo(selectionModel.selectedIndex)
        requestFocus()
    }
}