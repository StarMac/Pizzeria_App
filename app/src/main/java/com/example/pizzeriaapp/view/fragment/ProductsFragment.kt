package com.example.pizzeriaapp.view.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.ProductAdapter
import com.example.pizzeriaapp.databinding.FragmentProductsBinding
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.database.*

class ProductsFragment : BaseFragment<FragmentProductsBinding>(FragmentProductsBinding::inflate) {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productsArrayList : ArrayList<Pizza>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        productsRecyclerView = binding.recycleView
        productsRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        productsRecyclerView.setHasFixedSize(true)

        productsArrayList= arrayListOf<Pizza>()
        getProductData()
    }

    private fun getProductData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Pizza")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(productSnapshot in snapshot.children){
                        val product = productSnapshot.getValue(Pizza::class.java)
                        productsArrayList.add(product!!)
                    }
                    productsRecyclerView.adapter = ProductAdapter(productsArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }

        })
    }
}