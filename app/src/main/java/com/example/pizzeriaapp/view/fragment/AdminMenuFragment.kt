package com.example.pizzeriaapp.view.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.adapter.AdminMenuAdapter
import com.example.pizzeriaapp.adapter.MenuAdapter
import com.example.pizzeriaapp.databinding.FragmentAdminMenuBinding
import com.example.pizzeriaapp.viewmodel.AdminMenuViewModel
import com.example.pizzeriaapp.viewmodel.MenuViewModel
import com.google.android.material.textfield.TextInputEditText

class AdminMenuFragment :
    BaseFragment<FragmentAdminMenuBinding>(FragmentAdminMenuBinding::inflate) {
    private lateinit var adminMenuRecyclerView: RecyclerView
    private lateinit var adminMenuViewModel: AdminMenuViewModel
    private lateinit var adminMenuAdapter: AdminMenuAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        init()
    }

    private fun init() {
        adminMenuRecyclerView = binding.recycleView
        adminMenuRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        adminMenuAdapter = AdminMenuAdapter(ArrayList())
        adminMenuRecyclerView.adapter = adminMenuAdapter

        adminMenuViewModel = ViewModelProvider(this)[AdminMenuViewModel::class.java]

        adminMenuViewModel.menuLiveData.observe(viewLifecycleOwner) { products ->
            adminMenuAdapter.updateProducts(products)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.product_add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                showDialogAddPizza()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDialogAddPizza() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_product, null)

        val txtPizzaName: TextInputEditText = dialogView.findViewById(R.id.product_name_editText)
        val txtPizzaPrice: TextInputEditText = dialogView.findViewById(R.id.product_price_editText)
        val txtPizzaPhotoUrl: TextInputEditText =
            dialogView.findViewById(R.id.product_photo_editText)
        val txtPizzaDescription: TextInputEditText =
            dialogView.findViewById(R.id.product_description_editText)
        val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
        val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

        // Build the AlertDialog
        val builder = AlertDialog.Builder(context).setView(dialogView)
        val alertDialog = builder.create()

        // Set listeners
        btnCancel.setOnClickListener { alertDialog.dismiss() }
        btnConfirm.setOnClickListener {
            val name = txtPizzaName.text.toString()
            val price = txtPizzaPrice.text.toString().toIntOrNull()
            val photoUrl = txtPizzaPhotoUrl.text.toString()
            val description = txtPizzaDescription.text.toString()

            if (name.isNotBlank() && price != null && photoUrl.isNotBlank() && description.isNotBlank()) {
                adminMenuViewModel.addNewProduct(name, price, photoUrl, description)
                alertDialog.dismiss()
            } else {
                if (name.isBlank()) txtPizzaName.error = "Name cannot be empty"
                if (price == null) txtPizzaPrice.error = "Price cannot be empty"
                if (photoUrl.isBlank()) txtPizzaPhotoUrl.error = "Photo URL cannot be empty"
                if (description.isBlank()) txtPizzaDescription.error = "Description cannot be empty"
            }
        }

        val window = alertDialog.window
        if (window != null) {
            val params = WindowManager.LayoutParams().apply {
                copyFrom(window.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window.attributes = params
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        alertDialog.show()
    }
}