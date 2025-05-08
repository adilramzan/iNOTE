package com.example.iNOTE.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.iNOTE.data.dataclass.Note
import com.example.iNOTE.data.repository.NotesRepository

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotesRepository()

    private val _notes = MutableLiveData<List<Note>?>()
    val notes: LiveData<List<Note>?> = _notes

    private val _operationStatus = MutableLiveData<Pair<Boolean, String?>>()
    val operationStatus: LiveData<Pair<Boolean, String?>> = _operationStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun addNote(note: Note) {
        _isLoading.value = true
        repository.addNote(note) { success, message ->
            _isLoading.value = false
            _operationStatus.value = Pair(success, message)
        }
    }

    fun updateNote(noteId: String, updatedNote: Note) {
        _isLoading.value = true
        repository.updateNote(noteId, updatedNote) { success, message ->
            _isLoading.value = false
            _operationStatus.value = Pair(success, message)
        }
    }

    fun deleteNote(noteId: String) {
        _isLoading.value = true
        repository.deleteNote(noteId) { success, message ->
            _isLoading.value = false
            _operationStatus.value = Pair(success, message)
        }
    }

    fun getNotes() {
        _isLoading.value = true
        repository.getNotes { notes, error ->
            _isLoading.value = false
            if (notes != null) {
                _notes.value = notes
            } else {
                _operationStatus.value = Pair(false, error)
            }
        }
    }

    fun startListeningToNotes() {
        repository.listenToNotes { notes, error ->
            if (notes != null) {
                _notes.value = notes
            } else {
                _operationStatus.value = Pair(false, error)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}
