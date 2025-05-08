package com.example.iNOTE.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.iNOTE.data.dataclass.Note
import com.example.iNOTE.databinding.NoteItemBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongPress: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    private val fullNoteList = mutableListOf<Note>()

    inner class NoteViewHolder(
        private val binding: NoteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.noteTitle.text = note.title
            binding.noteContent.text = note.content

            binding.favNote.visibility = if (note.isPinned) ViewGroup.VISIBLE else ViewGroup.GONE

            val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date(note.timeStamp))
            binding.noteTime.text = formattedDate

            binding.root.setOnClickListener { onNoteClick(note) }

            binding.root.setOnLongClickListener {
                onNoteLongPress(note)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setData(notes: List<Note>) {
        fullNoteList.clear()
        fullNoteList.addAll(notes)
        submitSortedList(fullNoteList)
    }

    fun filter(query: String?) {
        val filtered = query?.let {
            fullNoteList.filter { note ->
                note.title.contains(it, ignoreCase = true) ||
                        note.content.contains(it, ignoreCase = true)
            }
        } ?: fullNoteList
        submitSortedList(filtered)
    }

    private fun submitSortedList(notes: List<Note>) {
        val sorted = notes.sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.timeStamp })
        submitList(sorted)
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
    }
}
