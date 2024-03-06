package com.example.pdforganizer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pdforganizer.adapters.PdfFilesAdapter
import com.example.pdforganizer.databinding.ActivityAllPdfBinding
import com.example.pdforganizer.dto.PdfFile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AllPdfActivity : AppCompatActivity(), PdfFilesAdapter.pdfClickListener {
    private lateinit var binding: ActivityAllPdfBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: PdfFilesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs") //this will create a folder called "pdfs" in realtime
        initRecyclerView()

        getAllPdfs()
    }

    private fun getAllPdfs() {
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<PdfFile>()
                snapshot.children.forEach{
                    val pdfFile = it.getValue(PdfFile::class.java)
                    if (pdfFile != null) {
                        tempList.add(pdfFile)
                    }
                }
                if(tempList.isEmpty()) Toast.makeText(this@AllPdfActivity, "No data found", Toast.LENGTH_SHORT).show()

                adapter.submitList(tempList)
                binding.progressbar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllPdfActivity, error.message.toString(), Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility = View.GONE
            }

        })
    }

    private fun initRecyclerView() {
        binding.pdfrecyclerView.setHasFixedSize(true)
        binding.pdfrecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = PdfFilesAdapter(this)
        binding.pdfrecyclerView.adapter = adapter
    }

    override fun onPdfClick(pdfFile: PdfFile) {
        val intent = Intent(this, PdfViewerActivity::class.java)
        intent.putExtra("fileName", pdfFile.fileName)
        intent.putExtra("downloadUrl", pdfFile.downloadUrl)
        startActivity(intent)
    }


}