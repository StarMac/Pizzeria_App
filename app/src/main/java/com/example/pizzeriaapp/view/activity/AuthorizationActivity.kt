package com.example.pizzeriaapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.pizzeriaapp.databinding.ActivityAuthorizationBinding
import com.example.pizzeriaapp.viewmodel.AuthorizationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthorizationActivity :
    BaseActivity<ActivityAuthorizationBinding>(ActivityAuthorizationBinding::inflate) {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: AuthorizationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setupViewModel()
        setupObservers()
        binding.authContainer.visibility = View.GONE // Hiding All Views
    }

    private fun init() {
        binding.signInTextButton.setOnClickListener { showSignIn() }
        binding.signUpTextButton.setOnClickListener { showSignUp() }
        binding.signInButton.setOnClickListener { signInWithEmailCheck() }
        binding.signUpButton.setOnClickListener { signUpWithEmailCheck() }
        binding.googleImageButton.setOnClickListener { signInWithGoogle() }

        auth = Firebase.auth

        // Configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("858280102305-4fdri7uf50q41skc4t017c5k9p51iuqq.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[AuthorizationViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.googleSignInResult.observe(this) { isSuccess ->
            if (isSuccess) {
                // Login with Google is successful, update the user interface
                val user = auth.currentUser
                updateUI(user)
            } else {
                // Google login failed, process error
                updateUI(null)
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            viewModel.handleGoogleSignInResult(data)
        }
    }

    private fun signUpWithEmailCheck() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()
        val name = binding.nameEditText.text.toString().trim()

        if (email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && name.isNotBlank() && password == confirmPassword) {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailEditText.error = "Please enter a valid email address"
                return
            }
            if(password.length < 6){
                binding.passwordEditText.error = "Password must be at least 6 characters long"
                return
            }
            viewModel.signUpWithEmail(email, password, name)
        } else {
            if (email.isBlank()) binding.emailEditText.error = "Email cannot be empty"
            if (password.isBlank()) binding.passwordEditText.error = "Password cannot be empty"
            if (confirmPassword.isBlank()) binding.confirmPasswordEditText.error = "Confirm password cannot be empty"
            else if (password != confirmPassword) binding.confirmPasswordEditText.error = "Passwords don't match"
            if (name.isBlank()) binding.nameEditText.error = "Name cannot be empty"
        }
    }

    private fun signInWithEmailCheck() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        var fieldsFilled = true

        if (email.isBlank()) {
            binding.emailEditText.error = "Email cannot be empty"
            binding.emailEditText.requestFocus()
            fieldsFilled = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Please enter a valid email address"
            binding.emailEditText.requestFocus()
            fieldsFilled = false
        }
        if (password.isBlank()) {
            binding.passwordEditText.error = "Password cannot be empty"
            binding.passwordEditText.requestFocus()
            fieldsFilled = false
        }

        if (fieldsFilled) {
            viewModel.signInWithEmail(email, password)
        }
    }

    @Suppress("DEPRECATION")
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            viewModel.getUserRole(user.uid).observe(this) { role ->
                val intent = when (role) {
                    "Client" -> Intent(applicationContext, MainActivity::class.java)
                    "Employee" -> Intent(applicationContext, EmployeeActivity::class.java)
                    "Admin" -> Intent(applicationContext, AdminActivity::class.java)
                    else -> {
                        // User role not found, handle error here
                        Log.d("AuthorizationActivity", "User role not found")
                        null
                    }
                }
                intent?.let {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    overridePendingTransition(0, 0) // Disables animation
                    finish()
                    overridePendingTransition(0, 0) // Disables animations
                }
            }
        } else {
            binding.authContainer.visibility = View.VISIBLE // Show views if user is not logged in
        }
    }
    private fun showSignIn() {
        binding.emailEditText.setText("")
        binding.passwordEditText.setText("")
        binding.confirmPasswordEditText.setText("")
        binding.nameEditText.setText("")
        binding.signInTextButton.visibility = View.GONE
        binding.repeatPasswordTextInputLayout.visibility = View.GONE
        binding.nameTextInputLayout.visibility = View.GONE
        binding.signUpButton.visibility = View.GONE
        binding.signUpTextButton.visibility = View.VISIBLE
        binding.forgotPasswordTextButton.visibility = View.VISIBLE
        binding.signInButton.visibility = View.VISIBLE
        binding.googleLoginText.visibility = View.VISIBLE
        binding.googleImageButton.visibility = View.VISIBLE
    }

    private fun showSignUp() {
        binding.emailEditText.setText("")
        binding.passwordEditText.setText("")
        binding.confirmPasswordEditText.setText("")
        binding.nameEditText.setText("")
        binding.signInTextButton.visibility = View.VISIBLE
        binding.repeatPasswordTextInputLayout.visibility = View.VISIBLE
        binding.nameTextInputLayout.visibility = View.VISIBLE
        binding.signUpButton.visibility = View.VISIBLE
        binding.signUpTextButton.visibility = View.GONE
        binding.forgotPasswordTextButton.visibility = View.GONE
        binding.signInButton.visibility = View.GONE
        binding.googleLoginText.visibility = View.GONE
        binding.googleImageButton.visibility = View.GONE
    }

    companion object {
        const val RC_SIGN_IN = 1001
    }
}