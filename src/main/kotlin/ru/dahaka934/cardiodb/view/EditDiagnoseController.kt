package ru.dahaka934.cardiodb.view

import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.stage.WindowEvent
import ru.dahaka934.cardiodb.data.Diagnose
import ru.dahaka934.cardiodb.data.MKB10
import ru.dahaka934.cardiodb.data.MKB10.Entry
import ru.dahaka934.cardiodb.fxlib.FXController
import ru.dahaka934.cardiodb.fxlib.FXHelper
import ru.dahaka934.cardiodb.fxlib.component.FilterableTreeItem
import ru.dahaka934.cardiodb.fxlib.component.FilterableTreeItem.Filter
import ru.dahaka934.cardiodb.util.SimpleLinkedTree
import java.util.concurrent.Callable

class EditDiagnoseController : FXController<Pane>() {
    @FXML lateinit var fieldType: ChoiceBox<String>
    @FXML lateinit var fieldCode: TextField
    @FXML lateinit var fieldInfo: TextField
    @FXML lateinit var fieldFilter: TextField

    @FXML lateinit var treeMkb10: TreeView<MKB10.Entry>

    @FXML lateinit var buttonSave: Button
    @FXML lateinit var buttonCancel: Button

    private var fullTree: FilterableTreeItem<MKB10.Entry>? = null

    var output: Diagnose? = null

    fun setDiagnose(diagnose: Diagnose) {
        fieldType.selectionModel.select(FXHelper.convDiagnosisType.toString(diagnose.type.value))
        fieldCode.text = diagnose.mkbID.value
        fieldInfo.text = diagnose.info.value

        output = diagnose
    }

    private fun createMkbTreeItem(node: SimpleLinkedTree.Node<MKB10.Entry>,
                                  filter: (MKB10.Entry) -> Boolean): FilterableTreeItem<MKB10.Entry>? {
        val item = FilterableTreeItem(node.entry).apply {
            node.childs.forEach {
                val ch = createMkbTreeItem(it, filter)
                if (ch != null) {
                    sourceChildren() += ch
                }
            }
        }

        return if (item.children.isNotEmpty() || filter(node.entry)) item else null
    }

    private fun createMkbTree(filter: (MKB10.Entry) -> Boolean): FilterableTreeItem<MKB10.Entry>? {
        val tree = MKB10.tree
        return FilterableTreeItem(Entry("", "root", "")).also { root ->
            tree.branches.forEach { branch ->
                val ch = createMkbTreeItem(branch, filter)
                if (ch != null) {
                    root.sourceChildren() += ch
                }
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
                    text = (if (!empty && item != null) {
                        String.format("%-8s : %s", item.id, item.info)
                    } else "")
                }
            }.apply {
                font = Font.font("Courier New")
                setOnMouseClicked {
                    val item = (it.source as TreeCell<MKB10.Entry>).item
                    if (item != null && it.clickCount == 2) {
                        fieldCode.text = item.id
                        fieldInfo.text = item.info
                    }
                }
            }
        }

        if (MKB10.isLoaded) {
            fullTree = createMkbTree { true }
            treeMkb10.root = fullTree
            treeMkb10.isShowRoot = false
            treeMkb10.isVisible = true

            fullTree?.filterProperty()?.bind(
                Bindings.createObjectBinding(Callable<Filter<Entry>> {
                    if (fieldFilter.text.isNullOrEmpty()) {
                        return@Callable null
                    }

                    object : Filter<Entry> {
                        override fun test(node: TreeItem<Entry>): Boolean {
                            val filter = fieldFilter.text

                            val value = node.value
                            val ret = value.id.startsWith(filter, ignoreCase = true) ||
                                      value.info.indexOf(filter, ignoreCase = true) >= 0
                            if (ret) {
                                var curr = node
                                while (curr.parent != null) {
                                    curr = curr.parent
                                    curr.isExpanded = true
                                }
                            }
                            return ret
                        }
                    }
                }, fieldFilter.textProperty()))
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
