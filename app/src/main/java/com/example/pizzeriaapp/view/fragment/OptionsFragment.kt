package com.example.pizzeriaapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.pizzeriaapp.databinding.FragmentOptionsBinding
import com.example.pizzeriaapp.view.activity.AuthorizationActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OptionsFragment : BaseFragment<FragmentOptionsBinding>(FragmentOptionsBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signOutButton.setOnClickListener{signOut()}
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(requireActivity(), AuthorizationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Make sure that the user cannot go back after exiting
        requireActivity().startActivity(intent)
        requireActivity().overridePendingTransition(0, 0) // Turns off animation
        requireActivity().finish()
        requireActivity().overridePendingTransition(0, 0) // Turns off animation
    }
}