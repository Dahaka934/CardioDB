package ru.dahaka934.cardiodb.view

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.stage.WindowEvent
import ru.dahaka934.cardiodb.data.Diagnose
import ru.dahaka934.cardiodb.data.MKB10
import ru.dahaka934.cardiodb.data.MKB10.Entry
import ru.dahaka934.cardiodb.util.SimpleLinkedTree
import ru.dahaka934.cardiodb.view.internal.Controller
import ru.dahaka934.cardiodb.view.internal.FXHelper

class EditDiagnoseController : Controller<Pane>() {
    @FXML lateinit var fieldType: ChoiceBox<String>
    @FXML lateinit var fieldCode: TextField
    @FXML lateinit var fieldInfo: TextField

    @FXML lateinit var treeMkb10: TreeView<MKB10.Entry>

    @FXML lateinit var buttonSave: Button
    @FXML lateinit var buttonCancel: Button

    @FXML lateinit var scroll: ScrollPane

    var output: Diagnose? = null

    fun setDiagnose(diagnose: Diagnose) {
        fieldType.selectionModel.select(FXHelper.convDiagnosisType.toString(diagnose.type.value))
        fieldCode.text = diagnose.mkbID.value
        fieldInfo.text = diagnose.info.value

        output = diagnose
    }

    private fun createMkbTreeItem(node: SimpleLinkedTree.Node<MKB10.Entry>): TreeItem<MKB10.Entry> {
        return TreeItem(node.entry).apply {
            node.childs.forEach {
                children += createMkbTreeItem(it)
            }
        }
    }

    @FXML fun initialize() {
        fieldType.items.addAll(enumValues<Diagnose.Type>().map { it.localName })
        fieldType.selectionModel.select(Diagnose.Type.MAIN.localName)

        buttonSave.setOnAction {
            var output = output
            if (output == null) {
                output = Diagnose()
                this.output = output
            }

            output.apply {
                type.value = FXHelper.convDiagnosisType.fromString(fieldType.value)
                mkbID.value = fieldCode.text
                info.value = fieldInfo.text
            }
            close()
        }

        buttonCancel.setOnAction {
            close()
        }

        treeMkb10.setCellFactory {
            object : TreeCell<MKB10.Entry>() {
                override fun updateItem(item: Entry?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (!empty && item != null) {
                        text = String.format("%-8s : %s", item.id, item.info)
                    }
                }
            }.apply {
                font = Font.font("Courier New")
                setOnMouseClicked {
                    val item = (it.source as TreeCell<MKB10.Entry>).item
                    if (it.clickCount == 2) {
                        fieldCode.text = item.id
                        fieldInfo.text = item.info
                    }
                }
            }
        }

        if (MKB10.isLoaded) {
            treeMkb10.isVisible = true

            val tree = MKB10.tree
            treeMkb10.root = TreeItem(MKB10.Entry("", "root", "")).also { root ->
                tree.branches.forEach { branch ->
                    root.children += createMkbTreeItem(branch)
                }
            }
            treeMkb10.isShowRoot = false
        } else {
            treeMkb10.isVisible = false
        }

        FXHelper.bindTabOnPressEnter(fieldType)
        FXHelper.bindTabOnPressEnter(fieldCode)
        FXHelper.bindTabOnPressEnter(fieldInfo)

        buttonSave.graphic = FXHelper.imageView("ok24")
        buttonCancel.graphic = FXHelper.imageView("cancel24")

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
