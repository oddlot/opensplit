package io.oddlot.opensplit.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import io.oddlot.opensplit.Application
import io.oddlot.opensplit.R
import io.oddlot.opensplit.adapters.GroupAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "GroupListFragment"

class GroupListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_group_list, container, false) as RecyclerView

        CoroutineScope(Dispatchers.IO).launch {
            val groups = Application.getDatabase(requireContext().applicationContext).groupDao().getAll()

            fragmentView.adapter = GroupAdapter(groups)
        }

        return inflater.inflate(R.layout.fragment_group_list, container, false)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val toolbarView = view.findViewById<MaterialToolbar>(R.id.toolbar)
//
//        (activity as AppCompatActivity).setSupportActionBar(toolbarView)
//        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
//        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//    }
}