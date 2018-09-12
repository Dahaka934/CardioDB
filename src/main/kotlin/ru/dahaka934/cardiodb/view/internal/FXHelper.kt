package ru.dahaka934.cardiodb.view.internal

import javafx.application.Platform
import javafx.event.Event
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType.*
import javafx.scene.control.ButtonBar.ButtonData.*
import javafx.scene.input.KeyCode.*
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import ru.dahaka934.cardiodb.data.Diagnose
import ru.dahaka934.cardiodb.data.Patient.Sex
import ru.dahaka934.cardiodb.data.Visit.Type
import ru.dahaka934.cardiodb.util.LocalizedEnumConverter
import java.io.PrintWriter
import java.io.StringWriter

object FXHelper {
    val convSex = LocalizedEnumConverter(Sex::class.java)
    val convVisitType = LocalizedEnumConverter(Type::class.java)
    val convDiagnosisType = LocalizedEnumConverter(Diagnose.Type::class.java)

    fun alertYesNo(): Alert {
        return Alert(CONFIRMATION).apply {
            buttonTypes.clear()
            buttonTypes.addAll(ButtonType("Да", YES), ButtonType("Нет", NO))
        }
    }

    fun showError(msg: String, e: Exception?) {
        Alert(ERROR).apply {
            title = "Error"
            headerText = msg

            if (e != null) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)

                val textArea = TextArea()
                textArea.text = sw.toString()

                val dialogPaneContent = VBox()
                dialogPaneContent.children.addAll(Label("Stack Trace:"), textArea)

                // Set content for Dialog Pane
                dialogPane.content = dialogPaneContent
            }
        }.showAndWait()
    }

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