package com.s3s3l.niflheim.markdown

import com.s3s3l.doc.Doc
import com.s3s3l.doc.RequestType
import com.s3s3l.niflheim.DataNode
import com.s3s3l.niflheim.DocBook
import com.s3s3l.niflheim.DocBuilder
import net.steppschuh.markdowngenerator.list.UnorderedList
import net.steppschuh.markdowngenerator.rule.HorizontalRule
import net.steppschuh.markdowngenerator.text.code.CodeBlock
import net.steppschuh.markdowngenerator.text.emphasis.BoldText
import net.steppschuh.markdowngenerator.text.heading.Heading

class MarkdownDocBuilder : DocBuilder {

    override fun build(book: DocBook): String {
        var builder = StringBuilder()
        builder.append(Heading(book.name, 1))
                .append("\n")
        for (doc in book.docs) {
            builder.append(buildDoc(doc))
        }

        return builder.toString()
    }

    private fun buildDoc(doc: Doc): String {
        var basic = mutableListOf<String>()
        basic.add("Request type: " + doc.requestType)
        when (doc.requestType) {
            RequestType.HTTP_OR_HTTPS -> {
                basic.add("Path: " + CodeBlock(doc.path))
                basic.add("Method: " + CodeBlock(doc.httpMethod))
            }
            RequestType.RPC -> basic.add("Action: " + CodeBlock(doc.path))
        }
        basic.add("Desc: " + BoldText(doc.desc))
        var builder = StringBuilder(HorizontalRule().serialize())
                .append("\n")
                .append(Heading(doc.name, 2))
                .append("\n")
                .append(Heading("Basic", 3))
                .append("\n")
                .append(UnorderedList(basic))
                .append("\n")
                .append(Heading("Request", 3))
                .append("\n")

        return builder.toString()
    }

    private fun toRow(preRow: Row?, node: DataNode, prefix: Int): Row {
        var row = Row()
        var currentRow = row;
        row.appendCol(node.name)
                .appendCol(node.desc)
                .appendCol(node.type.name)
                .appendCol(node.remark)
        row.prefix = prefix

        for (child in node.children){
            currentRow = toRow(currentRow, child, prefix + 1)
        }

        if(preRow != null) {
            preRow.next = row
        }
        return row
    }
}
