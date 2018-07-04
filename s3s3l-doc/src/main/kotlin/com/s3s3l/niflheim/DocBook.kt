package com.s3s3l.niflheim

import com.s3s3l.doc.Doc

class DocBook {
    var name: String = ""
    var docs: MutableList<Doc> = mutableListOf()

    fun appendDoc(doc: Doc) {
        this.docs.add(doc)
    }
}