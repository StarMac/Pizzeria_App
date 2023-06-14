package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.AdminBanListAdapter
import com.example.pizzeriaapp.databinding.FragmentAdminBanListBinding
import com.example.pizzeriaapp.viewmodel.AdminUserListViewModel

class AdminBanListFragment : BaseFragment<FragmentAdminBanListBinding>(FragmentAdminBanListBinding::inflate) {
    private lateinit var adminBanListRecyclerView: RecyclerView
    private lateinit var adminUserListViewModel: AdminUserListViewModel
    private lateinit var adminBanListAdapter: AdminBanListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        adminBanListRecyclerView = binding.adminBanListRecycleView
        adminBanListRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        adminBanListAdapter = AdminBanListAdapter(ArrayList())
        adminBanListRecyclerView.adapter = adminBanListAdapter

        adminUserListViewModel = ViewModelProvider(this)[AdminUserListViewModel::class.java]

        adminUserListViewModel.bannedUsersLiveData.observe(viewLifecycleOwner) { bannedUsers ->
            adminBanListAdapter.updateBanList(bannedUsers)
        }
    }
}