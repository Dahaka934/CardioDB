package ru.dahaka934.cardiodb.data

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.util.IOTools
import ru.dahaka934.cardiodb.util.SimpleLinkedTree
import java.util.concurrent.CompletableFuture

object MKB10 {

    data class Entry(val id: String, val info: String, val parent: String?) {
        override fun toString(): String = if (parent != null) "[$id <- $parent]" else "[$id]"
    }

    val tree = SimpleLinkedTree<Entry>()

    @Volatile var isLoaded = false
        private set

    fun reloadAsync(): CompletableFuture<Unit> {
        isLoaded = false
        return CompletableFuture.runAsync {
            val entryMap = parseFile()

            entryMap.values.forEach {
                tree.addEntry(it)
            }

            var lastId: String? = null
            var lastEntry: Entry? = null
            entryMap.values.toList().forEach {
                val parentId = it.parent
                if (parentId != null) {
                    if (parentId != lastId) {
                        lastId = parentId
                        lastEntry = entryMap[parentId]
                    }

                    val parent = lastEntry
                    if (parent != null) {
                        tree.addLink(parent, it)
                    }
                }
            }
            tree.genBranches()
        }.thenApply {
            isLoaded = true
            CardioDB.log("Reload MKB-10 data")
        }
    }

    private fun parseFile(): Object2ObjectLinkedOpenHashMap<String, Entry> {
        val entryMap = Object2ObjectLinkedOpenHashMap<String, Entry>()
        IOTools.resourceStream("/assets/mkb10.xlsx")?.buffered().use {
            val rows = XSSFWorkbook(it).getSheetAt(0).rowIterator()

            repeat(4) {
                if (rows.hasNext()) {
                    rows.next()
                }
            }

            while (rows.hasNext()) {
                val row = rows.next() as XSSFRow
                val cells = row.cellIterator()
                val id = cells.nextString()
                val info = cells.nextString()
                var parent = cells.nextString()
                if (parent.equals("null", ignoreCase = true)) {
                    parent = null
                }

                if (id != null && info != null) {
                    entryMap[id] = Entry(id, info, parent)
                }
            }
        }
        return entryMap
    }

    private fun Iterator<Cell>.nextString(): String? {
        return if (hasNext()) {
            val cell = next()
            if (cell.cellType === CellType.STRING) {
                cell.stringCellValue
            } else null
        } else null
    }
}