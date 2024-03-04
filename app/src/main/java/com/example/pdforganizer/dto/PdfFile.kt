package com.example.pdforganizer.dto

data class PdfFile(val fileName: String, val downloadUrl: String){
    //firebase needs empty constructor to pass the data
    constructor(): this("", "")
}
