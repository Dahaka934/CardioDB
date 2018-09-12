package ru.dahaka934.cardiodb.util

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFRun
import java.io.File

class DocCreator {
    companion object {
        const val styleHeader = "Top"
        const val styleText = "Text"
    }

    val doc = XWPFDocument(IOTools.resourceStream("/assets/template.docx"))

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

inline fun XWPFParagraph.createRun(text: String, bold: Boolean = false, block: XWPFRun.() -> Unit = {}) {
    createRun().apply {
        setText(text)
        if (bold) {
            isBold = bold
        }
        block()
    }
}