package ru.dahaka934.cardiodb.fxlib.component

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.TreeItem
import java.util.concurrent.Callable
import java.util.function.Predicate

class FilterableTreeItem<T>(value: T) : TreeItem<T>(value) {
    private val sourceList = FXCollections.observableArrayList<TreeItem<T>>()
    private val filteredList = FilteredList<TreeItem<T>>(this.sourceList)

    private val filter = SimpleObjectProperty<Filter<T>>()

    init {

        this.filteredList.predicateProperty().bind(
            Bindings.createObjectBinding(Callable<Predicate<in TreeItem<T>>> {
                Predicate { child ->
                    // Set the predicate of child items to force filtering
                    if (child is FilterableTreeItem<*>) {
                        val filterableChild = child as FilterableTreeItem<T>
                        filterableChild.filter.value = this.filter.get()
                    }
                    // If there is no predicate, keep this tree item
                    if (this.filter.get() == null)
                        return@Predicate true
                    // If there are children, keep this tree item
                    if (child.children.size > 0)
                        return@Predicate true
                    // Otherwise ask the TreeItemPredicate
                    this.filter.get().test(child)
                }
            }, this.filter))
        setHiddenFieldChildren(this.filteredList)
    }

    protected fun setHiddenFieldChildren(list: ObservableList<TreeItem<T>>) {
        try {
            val childrenField = TreeItem::class.java.getDeclaredField("children") //$NON-NLS-1$
            childrenField.isAccessible = true
            childrenField.set(this, list)
            val declaredField = TreeItem::class.java.getDeclaredField("childrenListener") //$NON-NLS-1$
            declaredField.isAccessible = true
            list.addListener(declaredField.get(this) as ListChangeListener<in TreeItem<T>>)
        } catch (e: NoSuchFieldException) {
            throw RuntimeException("Could not set TreeItem.children", e) //$NON-NLS-1$
        } catch (e: SecurityException) {
            throw RuntimeException("Could not set TreeItem.children", e)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("Could not set TreeItem.children", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Could not set TreeItem.children", e)
        }
    }

    fun filterProperty() = filter

    fun sourceChildren() = sourceList

    @FunctionalInterface
    interface Filter<T> {
        fun test(node: TreeItem<T>): Boolean
    }
}