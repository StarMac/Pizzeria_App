package com.example.pizzeriaapp.view.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.OrderAdapter
import com.example.pizzeriaapp.databinding.FragmentOrderListBinding
import com.example.pizzeriaapp.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class OrderListFragment : BaseFragment<FragmentOrderListBinding>(FragmentOrderListBinding::inflate) {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderArrayList : ArrayList<Order>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        auth = Firebase.auth
        orderRecyclerView = binding.orderListRecycleView
        orderRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        orderRecyclerView.setHasFixedSize(true)
        orderArrayList= arrayListOf<Order>()
        getOrderData()
    }

    private fun getOrderData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Order")
        val queryDatabase = databaseReference.orderByChild("uid").equalTo(auth.currentUser!!.uid) //TODO Отсортировать ещё по времени, а не только по uid
        queryDatabase.addValueEventListener(object : ValueEventListener {
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
                Log.w(TAG, "loadPost:onCancelled", error.toException())
            }

        })
    }
}