package com.example.myvault

class StorageData {
    var imageMemory: String? =null
    var pdfMemory: String? =null
    var totalMemory: String? =null

    constructor(){}

    constructor(imageMemory: String?,pdfMemory: String?, totalMemory:String? ){
        this.imageMemory=imageMemory
        this.pdfMemory=pdfMemory
        this.totalMemory=totalMemory

    }
}