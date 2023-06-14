package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AuthorizationViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val userCollectionRef: CollectionReference =
        FirebaseFirestore.getInstance().collection("User")
    private val banListCollectionRef: CollectionReference =
        FirebaseFirestore.getInstance().collection("BanList")


    private val _googleSignInResult = MutableLiveData<Boolean>()
    val googleSignInResult: LiveData<Boolean> get() = _googleSignInResult

    fun getUserRole(uid: String): LiveData<String> {
        val role = MutableLiveData<String>()

        userCollectionRef.document(uid).get().addOnSuccessListener { document ->
            if (document != null) {
                val user = document.toObject(User::class.java)
                role.value = user?.role
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }

        return role
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val newUser = User(user?.uid, name, user!!.email, "Client")
                    userCollectionRef.document(user.uid).set(newUser)
                    _googleSignInResult.value = true
                    Log.d(TAG, "Google sign should be success")
                } else {
                    _googleSignInResult.value = false
                }
            }
    }

    fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    isUserBanned(user?.uid) { isBanned ->
                        if (isBanned) {
                            // Если пользователь заблокирован, выходим из учетной записи и выводим соответствующее сообщение
                            Toast.makeText(getApplication(), "Your account is banned", Toast.LENGTH_LONG).show()
                            auth.signOut()
                        } else {
                            _googleSignInResult.value = true
                        }
                    }
                } else {
                    _googleSignInResult.value = false
                    Toast.makeText(getApplication(), "Incorrect login or password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    isUserBanned(user?.uid) { isBanned ->
                        if (isBanned) {
                            // Если пользователь заблокирован, выходим из учетной записи и выводим соответствующее сообщение
                            Toast.makeText(getApplication(), "Your account is banned", Toast.LENGTH_LONG).show()
                            auth.signOut()
                        } else {
                            // Если пользователь не заблокирован, продолжаем обычный процесс входа
                            userCollectionRef.document(user?.uid!!).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        userCollectionRef.document(user.uid).update("name", user.displayName)
                                    } else {
                                        val newUser = User(user.uid, user.displayName, user.email, "Client")
                                        userCollectionRef.document(user.uid).set(newUser)
                                    }
                                }
                            _googleSignInResult.value = true
                        }
                    }
                } else {
                    _googleSignInResult.value = false
                }
            }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            signInWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            _googleSignInResult.value = false
            Log.d(TAG, "Google sign in failed", e)
        }
    }

    private fun isUserBanned(uid: String?, callback: (Boolean) -> Unit) {
        if (uid != null) {
            banListCollectionRef.document(uid).get().addOnSuccessListener { document ->
                callback.invoke(document.exists())
            }.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        } else {
            callback.invoke(false)
        }
    }

}