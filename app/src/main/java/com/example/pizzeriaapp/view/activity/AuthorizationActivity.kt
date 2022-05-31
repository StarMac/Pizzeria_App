package com.example.pizzeriaapp.view.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.example.pizzeriaapp.databinding.ActivityAuthorizationBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthorizationActivity :
    BaseActivity<ActivityAuthorizationBinding>(ActivityAuthorizationBinding::inflate) {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding.signInTextButton.setOnClickListener { showSignIn() }
        binding.signUpTextButton.setOnClickListener { showSignUp() }
        binding.signInButton.setOnClickListener { signInWithEmail() }
        binding.signUpButton.setOnClickListener { signUpWithEmail() }
        binding.googleImageButton.setOnClickListener {
            signInWithGoogle()
        }

        auth = Firebase.auth

        //Configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("858280102305-4fdri7uf50q41skc4t017c5k9p51iuqq.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signUpWithEmail() {
        if (TextUtils.isEmpty(binding.emailEditText.text.toString())) {
            binding.emailEditText.error = "Email cannot be empty"
            binding.emailEditText.requestFocus()
        } else if (TextUtils.isEmpty(binding.passwordEditText.text.toString())) {
            binding.passwordEditText.error = "Password cannot be empty"
            binding.passwordEditText.requestFocus()
        } else if (binding.passwordEditText.text.toString() != binding.repeatPasswordEditText.text.toString()) {
            binding.repeatPasswordEditText.error = "Passwords don't match"
            binding.repeatPasswordEditText.requestFocus()
        } else {
            auth.createUserWithEmailAndPassword(
                binding.emailEditText.text.toString().trim(),
                binding.passwordEditText.toString().trim())
            val user = auth.currentUser
            updateUI(user)
        }
    }

    private fun signInWithEmail() {
        auth.signInWithEmailAndPassword(binding.emailEditText.text.toString().trim(), binding.passwordEditText.text.toString().trim())
        val currentUser: FirebaseUser? = auth.currentUser
        updateUI(currentUser)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        updateUI(currentUser)
    }

    //TODO Refactor to not deprecated
    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result returned from launching the intent from GoogleSighInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                //Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
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
        binding.repeatPasswordEditText.setText("")
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
        binding.repeatPasswordEditText.setText("")
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