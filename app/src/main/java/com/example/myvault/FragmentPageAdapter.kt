package com.example.myvault

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentPageAdapter(Fragment_manager:FragmentManager,lifecycle:Lifecycle):FragmentStateAdapter(Fragment_manager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        return if (position==0) {
            ImageFragment()
        }
        else
            PdfFragment()
    }

}