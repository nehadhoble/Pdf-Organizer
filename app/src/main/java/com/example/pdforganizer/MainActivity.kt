package com.example.pdforganizer

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.example.pdforganizer.databinding.ActivityMainBinding
import com.example.pdforganizer.dto.PdfFile
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.xml.parsers.DocumentBuilder

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var pdfFileUri: Uri? = null
    private lateinit var storagereference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inint()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.selectPdfButton.setOnClickListener {
            launcher.launch("application/pdf")
//            launcher.launch("image/*") //explore this
        }

        binding.uploadBtn.setOnClickListener{
            if(pdfFileUri != null){
                uploadPdfFiletoFirebase()
            }else{
                Toast.makeText(this, "Please select pdf first", Toast.LENGTH_LONG).show()
            }
        }

        binding.showAllBtn.setOnClickListener {
            val intent = Intent(this, AllPdfActivity::class.java)
            startActivity(intent)
        }
    }



    private fun inint() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storagereference = FirebaseStorage.getInstance().reference.child("pdfs") //this will create a node called "pdfs" in realtime db
        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs") //this will create a folder called "pdfs" in realtime storage db
    }

    //As OnActivityResult are depricated we will create launcher
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        pdfFileUri = uri
        val finalPdfName = uri?.let { DocumentFile.fromSingleUri(this, it)?.name }
        binding.fileName.text = finalPdfName.toString()
    }

    private fun uploadPdfFiletoFirebase() {
        val fileName = binding.fileName.text.toString()
        //below line creating a node called "timestamp" under this subNode will be "fileName"  -structure-> (pdfs/timeStamp/fileName
        val mStoragRef = storagereference.child("${System.currentTimeMillis()}").child(fileName)
        pdfFileUri.let {uri->
            if (uri != null) {
                mStoragRef.putFile(uri).addOnSuccessListener {
                   mStoragRef.downloadUrl.addOnSuccessListener { downloadUri ->
                       val pdfFile = PdfFile(fileName, downloadUri.toString()) //creating te instance of pdf file
                       databaseReference.push().key?.let{pushKey ->
                           databaseReference.child(pushKey).setValue(pdfFile)
                               .addOnSuccessListener {

                                   pdfFileUri = null
                                   binding.fileName.text = resources.getString(R.string.no_pdf_file_selected_yet)
                                   Toast.makeText(this,"UPloaded Successfully", Toast.LENGTH_LONG).show()
                                   if(binding.progressBar.isShown){
                                       binding.progressBar.visibility = View.GONE
                                   }


                               }
                               .addOnFailureListener{
                                   Toast.makeText(this,it.message.toString(), Toast.LENGTH_LONG).show()
                                   if(binding.progressBar.isShown){
                                       binding.progressBar.visibility = View.GONE
                                   }
                               }
                       }

                   }
                }.addOnProgressListener {uploadTask ->
                    val uploadingPercent = uploadTask.bytesTransferred * 100 / uploadTask.totalByteCount
                    binding.progressBar.progress = uploadingPercent.toInt()
                    if(!binding.progressBar.isShown){
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }.addOnFailureListener{
                    if(binding.progressBar.isShown){
                        binding.progressBar.visibility = View.GONE
                    }
                    Toast.makeText(this,it.message.toString(), Toast.LENGTH_LONG).show()
                }
            } else{
                Toast.makeText(this, "Pdf file url not found", Toast.LENGTH_LONG).show()
            }

        }
    }
}