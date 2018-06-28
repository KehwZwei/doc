package com.s3s3l.doc

import com.s3s3l.niflheim.DataNode

class Doc {
    var name: String = ""
    var requestType: RequestType = RequestType.HTTP_OR_HTTPS
    var httpMethod: HttpMethod = HttpMethod.POST
    var path: String = ""
    var desc: String = ""
    var request: Array<DataNode> = emptyArray()
    var response: Array<DataNode> = emptyArray()
}