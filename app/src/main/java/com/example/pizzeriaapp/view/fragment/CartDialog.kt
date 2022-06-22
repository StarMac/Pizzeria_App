package com.example.pizzeriaapp.view.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.databinding.DialogCartBinding
import java.lang.IllegalStateException

//class CartDialog(
//    private val onSubmitClickListener : () -> Unit
//) : DialogFragment() {
//    private lateinit var binding : DialogCartBinding
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//
//        return activity?.let{
//            val alertDialog = AlertDialog.Builder(it)
//            alertDialog.setView(requireFra().layoutInflater.inflate((R.layout.fragment_products, null)))
//            alertDialog.create()
//        }?:throw IllegalStateException("Fragment is null")
//    binding = DialogCartBinding.inflate(LayoutInflater.from(context))
//
//        val builder = AlertDialog.Builder(requireActivity())
//        builder.setView(binding.root)
//
//        binding.btnCartOrder.setOnClickListener {
//            onSubmitClickListener.invoke()
//        }
//
//        val dialog = builder.create()
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        return  dialog
//    }
//}