package com.example.iNOTE.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iNOTE.R
import com.example.iNOTE.databinding.ActivityHomeBinding
import com.example.iNOTE.presentation.adapter.NoteAdapter
import com.example.iNOTE.viewModel.AuthViewModel
import com.example.iNOTE.viewModel.NoteViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val notesViewModel: NoteViewModel by viewModels()
    private lateinit var noteAdapter: NoteAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.addNoteFab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                val intent = Intent(this, NoteEditorActivity::class.java)
                intent.putExtra("note", note)
                startActivity(intent)
            },
            onNoteLongPress = { note ->
                binding.bottomBar.visibility = View.VISIBLE

                binding.deleteButton.setOnClickListener {
                    notesViewModel.deleteNote(note.id)
                    binding.bottomBar.visibility = View.GONE
                }

                binding.editButton.setOnClickListener {
                    val intent = Intent(this, NoteEditorActivity::class.java)
                    intent.putExtra("note", note)
                    startActivity(intent)
                    binding.bottomBar.visibility = View.GONE
                }

                binding.favButton.setOnClickListener {
                    if (note.isPinned){
                        note.isPinned = false
                        notesViewModel.updateNote(note.id,note)
                        notesViewModel.getNotes()
                        binding.bottomBar.visibility = View.GONE
                        return@setOnClickListener
                    }
                    else{
                        note.isPinned = true
                        notesViewModel.updateNote(note.id,note)
                        notesViewModel.getNotes()
                        binding.bottomBar.visibility = View.GONE
                    }
                }
            }
        )
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = noteAdapter

        notesViewModel.notes.observe(this, Observer { notes ->
            noteAdapter.setData(notes ?: emptyList())
        })

        binding.notesRecyclerView.setOnTouchListener { _, _ ->
            binding.bottomBar.visibility = View.GONE
            false
        }

        binding.searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                noteAdapter.filter(newText)
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                authViewModel.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        notesViewModel.startListeningToNotes()
    }
}
