package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.adapter.AdminUserAdapter
import com.example.pizzeriaapp.databinding.FragmentAdminUserListBinding
import com.example.pizzeriaapp.viewmodel.AdminUserListViewModel

class AdminUserListFragment : BaseFragment<FragmentAdminUserListBinding>(FragmentAdminUserListBinding::inflate) {
    private lateinit var adminUserListRecyclerView: RecyclerView
    private lateinit var adminUserListViewModel: AdminUserListViewModel
    private lateinit var adminUserAdapter: AdminUserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        adminUserListRecyclerView = binding.adminUserListRecycleView
        adminUserListRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        adminUserAdapter = AdminUserAdapter(ArrayList())
        adminUserListRecyclerView.adapter = adminUserAdapter

        adminUserListViewModel = ViewModelProvider(this)[AdminUserListViewModel::class.java]

        adminUserListViewModel.activeUsersLiveData.observe(viewLifecycleOwner) { users ->
            adminUserAdapter.updateUsers(users)
        }
    }
}