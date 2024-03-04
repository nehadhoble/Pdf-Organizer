package com.example.pdforganizer

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.example.pdforganizer.databinding.ActivityMainBinding
import javax.xml.parsers.DocumentBuilder

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var pdfFileUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectPdfButton.setOnClickListener {
           launcher.launch("application/pdf")
//            launcher.launch("image/*") //explore this
        }
    }

    //As OnActivityResult are depricated we will create launcher
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        pdfFileUri = uri
        val finalPdfName = uri?.let { DocumentFile.fromSingleUri(this, it)?.name }
        binding.fileName.text = finalPdfName.toString()
    }
}