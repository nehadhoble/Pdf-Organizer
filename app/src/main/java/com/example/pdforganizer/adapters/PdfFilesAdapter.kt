package com.example.pdforganizer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdforganizer.databinding.EachPdfItemBinding
import com.example.pdforganizer.dto.PdfFile

class PdfFilesAdapter(private val listener: pdfClickListener): ListAdapter<PdfFile, PdfFilesAdapter.PdfFilesViewHolder>(PdfDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfFilesViewHolder {
       val binding = EachPdfItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PdfFilesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfFilesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class PdfFilesViewHolder(private  val binding : EachPdfItemBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
              listener.onPdfClick(getItem(adapterPosition))
            }
        }

        fun bind(data: PdfFile){
               binding.fileName.text = data.fileName
        }
    }
    //diff callback - diffUtil is comparator which works internally to calculate the difference between current list and updated/modified list
    class PdfDiffCallBack: DiffUtil.ItemCallback<PdfFile>(){
        override fun areItemsTheSame(oldItem: PdfFile, newItem: PdfFile): Boolean = oldItem.downloadUrl == newItem.downloadUrl

        override fun areContentsTheSame(oldItem: PdfFile, newItem: PdfFile): Boolean = oldItem == newItem
    }

    interface  pdfClickListener{
        fun onPdfClick(pdfFile: PdfFile)
    }


}