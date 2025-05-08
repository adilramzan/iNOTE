package com.example.iNOTE.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.iNOTE.data.dataclass.Note

class NotesRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private fun getUserNotesCollection() =
        db.collection("users")
            .document(auth.currentUser?.uid ?: throw IllegalStateException("User not logged in"))
            .collection("notes")

    fun addNote(note: Note, callback: (Boolean, String?) -> Unit) {
        val newDocRef = getUserNotesCollection().document()
        val noteWithId = note.copy(id = newDocRef.id)
        newDocRef.set(noteWithId)
            .addOnSuccessListener {
                callback(true, newDocRef.id)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun getNotes(callback: (List<Note>?, String?) -> Unit) {
        getUserNotesCollection().get()
            .addOnSuccessListener { querySnapshot ->
                val notes = querySnapshot.toObjects(Note::class.java)
                callback(notes, null)
            }
            .addOnFailureListener { e ->
                callback(null, e.message)
            }
    }

    fun updateNote(noteId: String?, updatedNote: Note, callback: (Boolean, String?) -> Unit) {
        if (noteId.isNullOrEmpty()) {
            callback(false, "Invalid note ID")
            return
        }

        getUserNotesCollection().document(noteId).set(updatedNote)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun deleteNote(noteId: String?, callback: (Boolean, String?) -> Unit) {
        if (noteId.isNullOrEmpty()) {
            callback(false, "Invalid note ID")
            return
        }

        getUserNotesCollection().document(noteId).delete()
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun listenToNotes(onUpdate: (List<Note>?, String?) -> Unit) {
        listenerRegistration?.remove()
        listenerRegistration = getUserNotesCollection()
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotesRepository", "Error listening: ${error.message}")
                    onUpdate(null, error.message)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    Log.d("NotesRepository", "Snapshot size: ${snapshot.size()}")
                    val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
                    onUpdate(notes, null)
                }
            }
    }


    fun removeListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
