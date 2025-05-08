package com.example.iNOTE.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.iNOTE.R
import com.example.iNOTE.databinding.ActivitySignUpBinding
import com.example.iNOTE.viewModel.AuthViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.signUpButton.setOnClickListener {
            val email: String = binding.emailEditText.text.toString()
            val password: String = binding.passwordEditText.text.toString()
            val confirmPassword: String = binding.confirmPasswordEditText.text.toString()

            if (email.isEmpty() ) {
                binding.emailEditText.error = "Enter email address"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordEditText.error = "Enter password"
                binding.passwordEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.passwordEditText.error = "Password must be at least 8 characters"
                binding.passwordEditText.requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                binding.confirmPasswordEditText.error = "Re-enter password"
                binding.confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword != password) {
                binding.confirmPasswordEditText.error = "Passwords do not match"
                binding.confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }

        authViewModel.signUpUser(email, password) { success, message ->
            if (success) {
                Toast.makeText(this, "Sign-up successful", Toast.LENGTH_SHORT).show()
                authViewModel.sendVerificationEmail() { success2, message2 ->
                    if (success2) {
                        // Email sent successfully
                        Toast.makeText(this, "Verification Email sent successfully", Toast.LENGTH_LONG).show()
                    } else {
                        // Failed to send email
                        Toast.makeText(this, message2, Toast.LENGTH_SHORT).show()
                    }
                }
                Toast.makeText(this, "Verification Email sent successfully", Toast.LENGTH_LONG).show()
                authViewModel.signOut()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

        binding.signInButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}