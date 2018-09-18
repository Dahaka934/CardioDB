package ru.dahaka934.cardiodb.util

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFRun
import java.io.File

class DocCreator {
    val doc = XWPFDocument(IOTools.resourceStream("/assets/template.docx"))

    init {
        repeat(doc.bodyElements.size) {
            doc.removeBodyElement(it)
        }
    }

    inline fun paragraph(block: XWPFParagraph.() -> Unit = {}) {
        doc.createParagraph {
            block()
        }
    }

    fun line(text: String = "", size: Int = 0, bold: Boolean = false) {
        doc.createParagraph {
            createRun(text, size, bold)
        }
    }

    fun lineSplit(text: String = "", size: Int = 0, bold: Boolean = false) {
        text.split("\n").forEach {
            line(it, size, bold)
        }
    }

    fun save(file: File): Boolean {
        return tryWithError("Не удалось сохранить файл '${file.name}'") {
            file.outputStream().buffered().use {
                doc.write(it)
                true
            }
        } ?: false
    }
}

inline fun XWPFDocument.createParagraph(block: XWPFParagraph.() -> Unit) {
    createParagraph().apply(block)
}

inline fun XWPFParagraph.createRun(text: String, size: Int = 0, bold: Boolean = false, block: XWPFRun.() -> Unit = {}) {
    createRun().apply {
        setText(text)
        if (size > 0) {
            fontSize = size
        }
        if (bold) {
            isBold = bold
        }
        block()
    }
}