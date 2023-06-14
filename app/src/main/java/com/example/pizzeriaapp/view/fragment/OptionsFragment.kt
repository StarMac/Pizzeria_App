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
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Убедитесь, что пользователь не сможет вернуться назад после выхода
        requireActivity().startActivity(intent)
        requireActivity().overridePendingTransition(0, 0) // Отключает анимацию
        requireActivity().finish()
        requireActivity().overridePendingTransition(0, 0) // Отключает анимацию
    }
}