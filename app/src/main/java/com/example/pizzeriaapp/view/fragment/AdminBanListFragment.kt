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
import com.example.pizzeriaapp.adapter.AdminBanListAdapter
import com.example.pizzeriaapp.databinding.FragmentAdminBanListBinding
import com.example.pizzeriaapp.viewmodel.AdminBanListViewModel

class AdminBanListFragment : BaseFragment<FragmentAdminBanListBinding>(FragmentAdminBanListBinding::inflate) {
    private lateinit var adminBanListRecyclerView: RecyclerView
    private lateinit var adminBanListViewModel: AdminBanListViewModel
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

        adminBanListViewModel = ViewModelProvider(this)[AdminBanListViewModel::class.java]

        adminBanListViewModel.bannedUsersLiveData.observe(viewLifecycleOwner) { bannedUsers ->
            adminBanListAdapter.updateBanList(bannedUsers)
        }
    }
}