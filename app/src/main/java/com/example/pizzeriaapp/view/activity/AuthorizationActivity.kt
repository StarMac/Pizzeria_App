package com.example.pizzeriaapp.view.activity

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.databinding.ActivityAuthorizationBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.sign

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
        auth = Firebase.auth

        //Configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("858280102305-4fdri7uf50q41skc4t017c5k9p51iuqq.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleImageButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

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