package com.perapps.notes.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.perapps.notes.R
import com.perapps.notes.data.local.entities.Note
import com.perapps.notes.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    var notes: List<Note>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onClickListener: ((Note) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    override fun getItemCount() = notes.size

    fun setOnItemClickListener(onItemClick: (Note) -> Unit) {
        this.onClickListener = onItemClick
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            val dateFormat = SimpleDateFormat("MMM d,yyyy", Locale.getDefault())
            val dateString = dateFormat.format(note.date)

            binding.apply {
                tvTitle.text = note.title
                if (!note.isSynced) {
                    ivSyncStatus.setImageResource(R.drawable.ic_close)
                } else {
                    ivSyncStatus.setImageResource(R.drawable.ic_check)
                }
                tvDate.text = dateString
                cvNote.setCardBackgroundColor(Color.parseColor(note.color))
                cvNote.setOnClickListener {
                    onClickListener?.let { click ->
                        click(note)
                    }
                }
            }
        }
    }
}