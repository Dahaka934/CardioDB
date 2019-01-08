package ru.dahaka934.cardiodb.view

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy.ALL_TABS
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Modality.APPLICATION_MODAL
import javafx.stage.StageStyle
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.fxlib.FXController

class MainController : FXController<BorderPane>() {

    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var tabDataBase: Tab

    @FXML lateinit var buttonSaveAll: Button
    @FXML lateinit var buttonCloseTab: Button

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

        fieldUser.text = CardioDB.app.user.toString()
        if (!CardioDB.app.user.isValid) {
            Platform.runLater { requestUser() }
        }

        val node = CardioDB.app.loadNode<VBox>("PatientTableLayout.fxml")
        tabDataBase.content = node
        CardioDB.app.getLoadedController<PatientTableController>().let {
            it.init(stage, node)
            it.init(this, "База данных")
            it.init()
            it.focusNode()
        }

        buttonSaveAll.disableProperty().bind(Bindings.`when`(CardioDB.app.registry.isDirty).then(false).otherwise(true))
        buttonSaveAll.setOnAction {
            CardioDB.app.registry.saveAllAsync()
        }

        buttonCloseTab.disableProperty().bind(Bindings.equal(0, tabPane.selectionModel.selectedIndexProperty()))
        buttonCloseTab.setOnAction {
            tabPane.selectionModel.selectedItem?.apply {
                onClosed?.handle(null)
                tabPane.tabs.remove(this)
            }
        }
    }

    fun <N : Node, T : ControllerTab<N>> openTab(name: String, fxmlPath: String): T {
        val node = CardioDB.app.loadNode<N>(fxmlPath)
        val controller = CardioDB.app.getLoadedController<T>()
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
        CardioDB.app.createWindow<EditUserController>("EditUserLayout.fxml") { controller ->
            title = "Пользователь"
            isResizable = false
            initModality(APPLICATION_MODAL)
            initStyle(StageStyle.UTILITY)
        }.showAndWait()

        fieldUser.text = CardioDB.app.user.toString()
    }

    @FXML fun onUserClick(e: MouseEvent) {
        if (e.clickCount == 2) {
            requestUser()
        }
    }

    open class ControllerTab<N : Node> : FXController<N>() {
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