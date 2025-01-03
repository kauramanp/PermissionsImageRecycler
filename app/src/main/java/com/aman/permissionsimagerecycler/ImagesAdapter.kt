package com.aman.permissionsimagerecycler

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aman.permissionsimagerecycler.databinding.LayoutAddImageBinding
import com.aman.permissionsimagerecycler.databinding.LayoutImageBinding

class ImagesAdapter(
    var list: ArrayList<Uri>,
    private inline val onDeleteClick: (Int) -> Unit,
    private inline val onAddClick: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
        private const val FOOTER_THRESHOLD = 10
    }

    override fun getItemViewType(position: Int): Int {
        return if (list.size < FOOTER_THRESHOLD && position == list.size) TYPE_FOOTER else TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return if (list.size < FOOTER_THRESHOLD) list.size + 1 else list.size
    }

    inner class ItemViewHolder(var view: LayoutImageBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(item: Uri, position: Int) {
            view.ivImage.setImageURI(item)
            view.ivRemove.setOnClickListener {
                onDeleteClick.invoke(position)
            }
        }
    }

    inner class FooterViewHolder(var view: LayoutAddImageBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind() {
            view.root.setOnClickListener {
                onAddClick.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOOTER) {
            FooterViewHolder(
                LayoutAddImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ItemViewHolder(
                LayoutImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            holder.bind(item, position)
        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }
}