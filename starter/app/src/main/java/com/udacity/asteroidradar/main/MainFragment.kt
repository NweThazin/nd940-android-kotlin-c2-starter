package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.domain.Asteroid

class MainFragment : Fragment(), AsteroidRadarAdapter.OnClickListener {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.ViewModelFactory(
                requireActivity().application,
                AsteroidDatabase.getInstance(requireContext()).asteroidDao
            )
        ).get(MainViewModel::class.java)
    }

    private lateinit var adapter: AsteroidRadarAdapter
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setHasOptionsMenu(true)
        setupAdapter()
        observeLiveData()
        loadAsteroids()

        return binding.root
    }

    private fun setupAdapter() {
        adapter = AsteroidRadarAdapter(this)
        binding.asteroidRecycler.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.defaultAsteroids.observe(
            viewLifecycleOwner,
            Observer { items -> updateAdapterList(items) })
        viewModel.asteroidsByFilter.observe(
            viewLifecycleOwner,
            Observer { items -> updateAdapterList(items) })
    }

    private fun updateAdapterList(items: List<Asteroid>?) {
        if (!items.isNullOrEmpty()) {
            viewModel.updateApiStatus(MainViewModel.ApiStatus.SUCCESS)
            adapter.submitList(items)
        }
    }

    private fun loadAsteroids() {
        viewModel.fetchAsteroids()
        viewModel.fetchPhotoOfDay()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val filterMenuItem = when (item.itemId) {
            R.id.show_week_menu -> MainViewModel.FilterMenuItem.WEEK
            R.id.show_today_menu -> MainViewModel.FilterMenuItem.TODAY
            R.id.show_saved_menu -> MainViewModel.FilterMenuItem.SAVED
            else -> null
        }
        viewModel.onMenuItemSelected(filterMenuItem)
        return true
    }

    override fun onClickItem(item: Asteroid) {
        findNavController().navigate(MainFragmentDirections.actionShowDetail(item))
    }
}
