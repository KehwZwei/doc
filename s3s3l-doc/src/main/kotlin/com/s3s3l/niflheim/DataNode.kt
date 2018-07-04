package com.s3s3l.niflheim

class DataNode() {
    var name: String = ""
    var desc: String = ""
    var type: DataType = DataType.STRING
    var remark: String = ""
    var children: MutableList<DataNode> = mutableListOf<DataNode>()

    constructor(name: String, desc: String, type: DataType) : this() {
        this.name = name
        this.desc = desc
        this.type = type
    }

    constructor(name: String, desc: String, type: DataType, remark: String) : this() {
        this.name = name
        this.desc = desc
        this.type = type
        this.remark = remark
    }

    fun appendChildren(node: DataNode) {
        this.children.add(node)
    }

    fun appendChildren(node: List<DataNode>) {
        this.children.addAll(node)
    }
}