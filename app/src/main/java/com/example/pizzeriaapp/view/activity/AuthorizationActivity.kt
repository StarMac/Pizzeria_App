package com.example.pizzeriaapp.view.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.pizzeriaapp.databinding.ActivityAuthorizationBinding
import com.example.pizzeriaapp.model.Order
import com.example.pizzeriaapp.model.Pizza
import com.example.pizzeriaapp.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AuthorizationActivity :
    BaseActivity<ActivityAuthorizationBinding>(ActivityAuthorizationBinding::inflate) {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    //TODO delete test
    private lateinit var userDatabaseRef : DatabaseReference
    private lateinit var pizzaDatabaseRef : DatabaseReference
    private lateinit var orderDatabaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding.signInTextButton.setOnClickListener { showSignIn() }
        binding.signUpTextButton.setOnClickListener { showSignUp() }
        binding.signInButton.setOnClickListener { signInWithEmailCheck() }
        binding.signUpButton.setOnClickListener { signUpWithEmailCheck() }
        binding.googleImageButton.setOnClickListener {
            signInWithGoogle()
        }

        auth = Firebase.auth
        //TODO delete test
        userDatabaseRef = Firebase.database.getReference("User")
        pizzaDatabaseRef = Firebase.database.getReference("Pizza")
        orderDatabaseRef = Firebase.database.getReference("Order")


        //Configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("858280102305-4fdri7uf50q41skc4t017c5k9p51iuqq.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
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
            signUpWithEmail(binding.emailEditText.text.toString().trim(),
                binding.passwordEditText.text.toString().trim())
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
            signInWithEmail(binding.emailEditText.text.toString().trim(),
                binding.passwordEditText.text.toString().trim())
        }
    }

    private fun signUpWithEmail(email : String, password : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //TODO delete test
                    val newUser = User(user!!.email)
                    userDatabaseRef.child(user.uid).setValue(newUser)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun signInWithEmail(email : String, password : String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
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
                    //TODO delete test
                    val newUser = User(user!!.email)
                    userDatabaseRef.child(user.uid).setValue(newUser)

                    //TODO delete test 2
                    testFirebase(user)
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

        private fun testFirebase(user: FirebaseUser?){
            if (user != null){
                val pizza1 = Pizza("Hawaiian Pizza", "25",
                    "https://www.jessicagavin.com/wp-content/uploads/2020/07/hawaiian-pizza-16-1200.jpg",
                    "mozzarella, pineapple, bacon, oregano, ounces, pizza sauce")

                val pizza2 = Pizza("Pizza With Tomatoes", "21",
                    "https://eclecticrecipes.com/wp-content/uploads/2011/10/zucchini-pizza.jpg",
                    "mozzarella, tomatoes, bacon, oregano, ounces, pizza sauce")
                val sdf = SimpleDateFormat("dd MM yyyy HH:mm")
                val calendar = Calendar.getInstance()
                val orderDate = sdf.format(calendar.time)
                val order1 = Order(orderDate,pizza1.name,pizza1.photo,"Status: Waiting",pizza1.price + " uah",user.uid)
                val order2 = Order(orderDate,pizza2.name,pizza2.photo,"Status: Waiting",pizza2.price + " uah",user.uid)
                pizzaDatabaseRef.child(pizza1.name!!).setValue(pizza1)
                pizzaDatabaseRef.child(pizza2.name!!).setValue(pizza2)
                orderDatabaseRef.child(order1.time + " " + order1.uId).setValue(order1)
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