package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.pizzeriaapp.databinding.FragmentMenuBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MenuFragment : BaseFragment<FragmentMenuBinding>(FragmentMenuBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signOutButton.setOnClickListener{signOut()}
    }

    private fun signOut() {
        Firebase.auth.signOut()
        requireActivity().finish()
    }
}