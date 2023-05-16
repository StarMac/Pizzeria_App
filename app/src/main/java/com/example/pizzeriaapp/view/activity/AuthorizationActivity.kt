package com.example.pizzeriaapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
                // Вход с помощью Google успешен, обновите пользовательский интерфейс
                val user = auth.currentUser
                updateUI(user)
            } else {
                // Вход с помощью Google не удался, обработайте ошибку
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
        if (TextUtils.isEmpty(binding.emailEditText.text.toString())) {
            binding.emailEditText.error = "Email cannot be empty"
            binding.emailEditText.requestFocus()
        } else if (TextUtils.isEmpty(binding.passwordEditText.text.toString())) {
            binding.passwordEditText.error = "Password cannot be empty"
            binding.passwordEditText.requestFocus()
        } else if (binding.passwordEditText.text.toString() != binding.confirmPasswordEditText.text.toString()) {
            binding.confirmPasswordEditText.error = "Passwords don't match"
            binding.confirmPasswordEditText.requestFocus()
        } else {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            viewModel.signUpWithEmail(email, password)
        }
    }

    private fun signInWithEmailCheck() {
        if (TextUtils.isEmpty(binding.emailEditText.text.toString())) {
            binding.emailEditText.error = "Email cannot be empty"
            binding.emailEditText.requestFocus()
        } else if (TextUtils.isEmpty(binding.passwordEditText.text.toString())) {
            binding.passwordEditText.error = "Password cannot be empty"
            binding.passwordEditText.requestFocus()
        } else {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
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
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(EXTRA_NAME, user.displayName)
            startActivity(intent)
        }
    }
    private fun showSignIn() {
        binding.emailEditText.setText("")
        binding.passwordEditText.setText("")
        binding.confirmPasswordEditText.setText("")
        binding.signInTextButton.visibility = View.GONE
        binding.repeatPasswordTextInputLayout.visibility = View.GONE
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
        binding.signInTextButton.visibility = View.VISIBLE
        binding.repeatPasswordTextInputLayout.visibility = View.VISIBLE
        binding.signUpButton.visibility = View.VISIBLE
        binding.signUpTextButton.visibility = View.GONE
        binding.forgotPasswordTextButton.visibility = View.GONE
        binding.signInButton.visibility = View.GONE
        binding.googleLoginText.visibility = View.GONE
        binding.googleImageButton.visibility = View.GONE
    }

    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA NAME"
    }
}