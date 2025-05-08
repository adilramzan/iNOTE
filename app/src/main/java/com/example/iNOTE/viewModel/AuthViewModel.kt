package com.example.iNOTE.viewModel

import androidx.lifecycle.ViewModel
import com.example.iNOTE.data.repository.FirebaseRepository

class AuthViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    fun isUserLoggedIn(): Boolean {
        return firebaseRepository.isUserLoggedIn()
    }

    fun signOut() {
        firebaseRepository.signOut()
    }

    fun signUpUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseRepository.signUpUser(email, password, callback)
    }

    fun signInUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseRepository.signInUser(email, password, callback)
    }

    fun sendPasswordResetEmail(email: String, callback: (Boolean, String?) -> Unit) {
        firebaseRepository.sendPasswordResetEmail(email, callback)
    }

    fun sendVerificationEmail(callback: (Boolean, String?) -> Unit) {
        firebaseRepository.sendVerificationEmail(callback)
    }

    fun checkIfEmailVerified():Boolean{
      return  firebaseRepository.isEmailVerified()
    }

    fun getCurrentUserId(): String? {
        return firebaseRepository.getCurrentUserId()
    }

    fun deleteUserData(callback: (Boolean, String?) -> Unit) {
        firebaseRepository.deleteUser(callback)
    }
}