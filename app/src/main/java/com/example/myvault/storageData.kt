package com.example.myvault

class StorageData {
     var totalStorage: String? = null
     var imageMemory: String? =null
     var pdfMemory: String? =null
    constructor() {}

    constructor(totalStorage:String, imageMemory:String, PDFMemory:String)
    {
        this.totalStorage=totalStorage
        this.pdfMemory=PDFMemory
        this.imageMemory=imageMemory
    }

}