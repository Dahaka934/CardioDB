package ru.dahaka934.cardiodb.view

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.TabPane.TabClosingPolicy.ALL_TABS
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.StageStyle
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.view.internal.Controller

class MainController : Controller<BorderPane>() {

    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var tabDataBase: Tab

    @FXML lateinit var buttonSaveAll: Button

    @FXML lateinit var fieldUser: Label

    @FXML fun initialize() {
        tabPane.tabClosingPolicy = ALL_TABS
    }

    override fun init() {
        node.setOnKeyPressed {
            if (it.code == KeyCode.ESCAPE) {
                Platform.runLater { tabPane.selectionModel.selectedItem.content.requestFocus() }
            }
        }

        fieldUser.textProperty().bind(CardioDB.user)
        if (fieldUser.text.isNullOrEmpty()) {
            Platform.runLater { requestUser() }
        }

        val node = CardioDB.loadNode<VBox>("PatientTableLayout.fxml")
        tabDataBase.content = node
        CardioDB.getController<PatientTableController>().let {
            it.init(stage, node)
            it.init(this, "База данных")
            it.init()
            it.focusNode()
        }

        buttonSaveAll.disableProperty().bind(Bindings.`when`(CardioDB.registry.isDirty).then(false).otherwise(true))
        buttonSaveAll.setOnAction {
            CardioDB.registry.saveOnExitAsync()
        }
    }

    fun <N : Node, T : ControllerTab<N>> openTab(name: String, fxmlPath: String): T {
        val node = CardioDB.loadNode<N>(fxmlPath)
        val controller = CardioDB.getController<T>()
        controller.let {
            it.init(stage, node)
            it.init(this, name)
            it.init()
        }
        val tab = Tab()
        tab.text = name
        tab.content = node
        tab.isClosable = true
        tab.setOnClosed {
            controller.onClose()
        }
        tabPane.tabs.add(tab)
        tabPane.selectionModel.select(tab)
        return controller
    }

    fun requestUser() {
        val res = TextInputDialog(CardioDB.user.value).apply {
            title = "Пользователь"
            headerText = "Введите имя пользователя"
            initStyle(StageStyle.UTILITY)
        }.showAndWait()

        if (res.isPresent) {
            CardioDB.user.value = res.get()
        }
    }

    @FXML fun onUserClick(e: MouseEvent) {
        if (e.clickCount == 2) {
            requestUser()
        }
    }

    open class ControllerTab<N : Node> : Controller<N>() {
        lateinit var main: MainController
            private set
        var tabName: String = ""

        fun init(main: MainController, tabName: String) {
            this.main = main
            this.tabName = tabName
        }

        open fun onClose() {}
    }
}