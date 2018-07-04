package com.s3s3l.niflheim.markdown

class Row {
    var cols: MutableList<String> = mutableListOf()
    var prefix: Int = 0
    var next: Row? = null

    fun appendCol(col: String): Row {
        this.cols.add(col)
        return this
    }
}