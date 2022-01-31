package io.oddlot.opensplit.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import io.oddlot.opensplit.R

private const val TAG = "GroupFragment"

class GroupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_group, container, false)
        val toolbarView = fragmentView.findViewById<MaterialToolbar>(R.id.toolbar)

        (activity as AppCompatActivity).supportActionBar?.title = "ASDASD"

        return inflater.inflate(R.layout.fragment_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbarView = view.findViewById<MaterialToolbar>(R.id.toolbar)

        (activity as AppCompatActivity).setSupportActionBar(toolbarView)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}