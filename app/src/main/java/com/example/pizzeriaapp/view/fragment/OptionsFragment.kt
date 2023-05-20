package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import com.example.pizzeriaapp.databinding.FragmentOptionsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OptionsFragment : BaseFragment<FragmentOptionsBinding>(FragmentOptionsBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signOutButton.setOnClickListener{signOut()}
    }

    private fun signOut() {
        Firebase.auth.signOut()
        requireActivity().finish()
    }
}