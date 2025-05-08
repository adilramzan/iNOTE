package com.example.iNOTE.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.iNOTE.R
import com.example.iNOTE.data.dataclass.Note
import com.example.iNOTE.databinding.ActivityNoteEditorBinding
import com.example.iNOTE.viewModel.NoteViewModel
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class NoteEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteEditorBinding
    private val noteViewModel: NoteViewModel by viewModels()

    private var note: Note? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        note = intent.getParcelableExtra("note")

        note?.let { note ->
            binding.noteTitle.setText(note.title)
            binding.noteContent.setText(note.content)

            if (note.imageUrl.isNotEmpty()) {
                val uri = Uri.parse(note.imageUrl)
                imageUri = uri
                binding.noteImage.setImageURI(uri)
                binding.noteImage.visibility = android.view.View.VISIBLE
            }

            val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date(note.timeStamp))
            binding.noteTime.text = formattedDate

            note.tags.forEach { tag ->
                addTagChip(tag)
            }
        } ?: finish()

        binding.deleteButton.setOnClickListener {
            note?.let { note ->
                noteViewModel.deleteNote(note.id)
                finish()
            }
        }

        binding.noteImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }


        binding.editButton.setOnClickListener {
            val title = binding.noteTitle.text.toString()
            val content = binding.noteContent.text.toString()
            val timeStamp = System.currentTimeMillis()
            val finalImageUrl = imageUri?.toString() ?: note?.imageUrl.orEmpty()

            val tags = mutableListOf<String>()
            for (i in 0 until binding.tagChipGroup.childCount) {
                val chip = binding.tagChipGroup.getChildAt(i) as Chip
                tags.add(chip.text.toString())
            }

            note?.let {
                val updatedNote = it.copy(
                    title = title,
                    content = content,
                    timeStamp = timeStamp,
                    imageUrl = finalImageUrl,
                    tags = tags
                )
                noteViewModel.updateNote(updatedNote.id, updatedNote)
                finish()
            }
        }

        binding.addTagButton.setOnClickListener {
            val tag = binding.tagInput.text.toString().trim()

            if (tag.isNotEmpty()) {
                addTagChip(tag)
                binding.tagInput.setText("")
            } else {
                Toast.makeText(this, "Tag cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTagChip(tag: String) {
        val chip = Chip(this)
        chip.text = tag
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            binding.tagChipGroup.removeView(chip)
        }
        binding.tagChipGroup.addView(chip)
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imageUri = it
                binding.noteImage.setImageURI(it)
                binding.noteImage.visibility = android.view.View.VISIBLE
            }
        }
}
