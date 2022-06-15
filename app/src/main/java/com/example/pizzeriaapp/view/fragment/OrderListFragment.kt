package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.OrderAdapter
import com.example.pizzeriaapp.databinding.FragmentOrderListBinding
import com.example.pizzeriaapp.model.Order
import com.google.firebase.database.*

class OrderListFragment : BaseFragment<FragmentOrderListBinding>(FragmentOrderListBinding::inflate) {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderArrayList : ArrayList<Order>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        orderRecyclerView = binding.orderListRecycleView
        orderRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        orderRecyclerView.setHasFixedSize(true)

        orderArrayList= arrayListOf<Order>()
        getProductData()
    }

    private fun getProductData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Order")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(orderSnapshot in snapshot.children){
                        val order = orderSnapshot.getValue(Order::class.java)
                        orderArrayList.add(order!!)
                    }
                    orderRecyclerView.adapter = OrderAdapter(orderArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}