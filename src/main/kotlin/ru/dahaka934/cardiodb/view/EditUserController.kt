package ru.dahaka934.cardiodb.view

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.stage.WindowEvent
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.fxlib.FXController
import ru.dahaka934.cardiodb.fxlib.FXHelper

class EditUserController : FXController<Pane>() {
    @FXML lateinit var fieldName: TextField
    @FXML lateinit var fieldPlace: TextField
    @FXML lateinit var comboBoxPlaces: ComboBox<String>

    @FXML lateinit var buttonSave: Button
    @FXML lateinit var buttonCancel: Button

    @FXML fun initialize() {
        comboBoxPlaces.setOnAction {
            fieldPlace.text = comboBoxPlaces.value
        }

        val invalidListener = InvalidationListener {
            val v = fieldName.text.isEmpty() || fieldPlace.text.isEmpty()
            buttonSave.isDisable = v
            buttonCancel.isDisable = v
        }

        fieldName.textProperty().addListener(invalidListener)
        fieldPlace.textProperty().addListener(invalidListener)

        val user = CardioDB.app.user
        fieldName.text = user.name
        fieldPlace.text = user.place
        comboBoxPlaces.items.addAll(user.oldPlaces)


        buttonSave.setOnAction {
            user.name = fieldName.text
            user.place = fieldPlace.text
            close()
        }

        buttonCancel.setOnAction {
            close()
        }

        Platform.runLater { fieldName.requestFocus() }

        buttonSave.graphic = FXHelper.imageView("ok24")
        buttonCancel.graphic = FXHelper.imageView("cancel24")

        FXHelper.bindTabOnPressEnter(fieldName)
        FXHelper.bindTabOnPressEnter(fieldPlace)

        FXHelper.bindClickOnPressEnter(buttonSave)
        FXHelper.bindClickOnPressEnter(buttonCancel)
    }

    override fun init() {
        node.setOnKeyPressed {
            if (it.code == KeyCode.ESCAPE) {
                close()
            }
        }
    }

    fun close() {
        stage.fireEvent(WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
    }
}