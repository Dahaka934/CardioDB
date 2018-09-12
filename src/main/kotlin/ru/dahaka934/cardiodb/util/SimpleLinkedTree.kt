package ru.dahaka934.cardiodb.util

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList

class SimpleLinkedTree<E> {
    class Node<E>(val entry: E, var parent: Node<E>?) {
        val childs = ObjectArrayList<Node<E>>()

        override fun toString() = if (parent != null) "[$entry <- $parent]" else "[$entry]"
    }

    val entries = Object2ObjectLinkedOpenHashMap<E, Node<E>>()
    val branches = ObjectArrayList<Node<E>>()

    fun clear() {
        entries.clear()
        branches.clear()
    }

    fun addEntry(entry: E) {
        entries[entry] = Node(entry, null)
    }

    fun addLink(from: E, to: E) {
        val nodeTo = entries[to] ?: return
        val nodeFrom = entries[from] ?: return
        nodeTo.parent = nodeFrom
        nodeFrom.childs += nodeTo
    }

    fun genBranches() {
        branches.clear()
        branches.addAll(entries.values.filter { it.parent == null })
    }
}