package io.oddlot.opensplit.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.oddlot.opensplit.OnSwipeTouchListener
import io.oddlot.opensplit.R
import io.oddlot.opensplit.adapters.AllocationAdapter
import io.oddlot.opensplit.round
import kotlinx.android.synthetic.main.fragment_expense_edit.*

private const val TAG = "TicketFragment"

class editExpenseFragment : Fragment() {

    companion object {
        fun newInstance() = editExpenseFragment()
    }

    private lateinit var vm: ExpenseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense_edit, container, false)

        // Cache views
        val etTotalAmount = view.findViewById<EditText>(R.id.totalAmount)
//        val amountEditLayout = view.findViewById<TextInputLayout>(R.id.etLayoutAmount)
        val allocationsRV = view.findViewById<RecyclerView>(R.id.rvPayees)
        val paidBySpinner = view.findViewById<Spinner>(R.id.paidBySpinner).apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, arrayOf("Thomas", "Brian"))
        }
        val recipientChips = view.findViewById<ChipGroup>(R.id.recipientChips)
        val allChip = view.findViewById<Chip>(R.id.allMembersChip).apply {
            setOnCheckedChangeListener { btn, checked ->
                if (checked) {
                    btn.isChecked = checked

                    // Uncheck individual chips
                    recipientChips.forEach {
                        (it as Chip).isChecked = false
                    }
                }
            }
        }
        val allocateBtn = view.findViewById<MaterialButton>(R.id.allocateBtn)
        val addPayeeBtn = view.findViewById<MaterialButton>(R.id.btnAddPayee)

        vm = ViewModelProvider(this).get(ExpenseViewModel::class.java).apply {
            allocationsRV.adapter = AllocationAdapter(this.allocations.value!!, this)
            allocationsRV.layoutManager = LinearLayoutManager(context)
        }

        vm.amountPaid.observe(viewLifecycleOwner) {
            if (etTotalAmount.text.isNullOrBlank() || it > etTotalAmount.text.toString().toDouble()) {
                etTotalAmount.text = SpannableStringBuilder(it.round(2).toString())
            }
            var allocated = 0.0
            vm.allocations.value?.forEach { item ->
                allocated += item.amount
            }

            val balance = vm.amountPaid.value!! - allocated
        }

        vm.allocations.observe(viewLifecycleOwner) {
            var allocated = 0.0
            it.forEach { item ->
                allocated += item.amount
            }

            val balance = if (vm.amountPaid.value != null) vm.amountPaid.value!! else 0.0 - allocated
        }

        recipientChips.addView(Chip(requireContext()).apply { text = "Created Chip"; isCheckable = true; setOnCheckedChangeListener { btn, checked ->
                if (checked) {
                    allChip.isChecked = false
                }
            }
        })

        // Listeners
        etTotalAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: Text change")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { editText ->
                    if (editText.isNotEmpty() && editText.toString() != ".") {
                        val amountPaid = editText.toString().toDouble()

                        vm.amountPaid.value = amountPaid
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        etTotalAmount.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeTop() {
                allocate(view, vm)
            }
        })
        etTotalAmount.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeRight() {
                allocate(view, vm)
            }
        })
        etTotalAmount.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                allocate(view, vm)
            }
        })

        view.findViewById<MaterialButton>(R.id.clearButton).apply {
            setOnClickListener {
                var tmp = vm.allocations.value
                tmp?.forEach {
                    if (!it.locked) {
                        it.amount = 0.0
                    }
                }

                vm.allocations.value = tmp
                Toast.makeText(this.context, "Entries cleared", Toast.LENGTH_SHORT).show()

                allocationsRV.adapter?.notifyDataSetChanged()
            }
        }

        /**
         * Subtracts locked fragment_allocations from total amount
         * and allocates the balance equally among
         * unlocked payees.
         *
         * Balance can be negative if locked allocation
         * exceeds total amount.
         */
        allocateBtn.setOnClickListener {
            allocate(view, vm)
        }

        addPayeeBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Add Payee")
                val imm = (activity as AppCompatActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                val et = EditText(context).apply {
                    val lp = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(resources.getDimensionPixelSize(R.dimen.DEFAULT_MARGIN_HORIZONTAL), resources.getDimensionPixelSize(R.dimen.DEFAULT_MARGIN_VERTICAL), resources.getDimensionPixelSize(R.dimen.DEFAULT_MARGIN_HORIZONTAL), 0)
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    isSingleLine = true
                    showSoftInputOnFocus = true
                    layoutParams = lp

                    setOnDismissListener {
                        imm.hideSoftInputFromWindow(this.windowToken, 0)
                    }

                    requestFocus()
                }

                // Show soft keyboard
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)

                val container = FrameLayout(requireContext())
                container.addView(et)
                setView(container)

                setPositiveButton("OK") {  dialog, which ->
                    if (et.text.isBlank()) {
                        et.error = "Name can't be empty!"
                    } else {
                        val tmp = vm.allocations.value!!
                        val newPayee = et.text.toString()

                        tmp.add(ExpenseViewModel.AllocationItem(newPayee, 0.0))

                        allocationsRV.adapter?.notifyDataSetChanged()
                        imm.hideSoftInputFromWindow(et.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                    }
                }
                setNegativeButton("Cancel") { dialog, which ->
                }
                show()
            }

        }

        return view
    }

    private fun allocate(view: View, vm: ExpenseViewModel) {
        var lockedAllocation = 0.0

        // Sum up locked allocation amounts
        vm.allocations.value!!.forEach { item ->
            if (item.locked) lockedAllocation += item.amount
        }
        val amount = if (vm.amountPaid.value != null) vm.amountPaid.value!! else 0.0
        val unlockedBalance = amount - lockedAllocation

        if (unlockedBalance > 0.0) {
            val allocation = unlockedBalance / vm.allocations.value!!.filter { !it.locked }.size
            Log.d(TAG, "Allocation: $allocation")
            val tmp = vm.allocations.value!!
            tmp.forEach {
                if (!it.locked) it.amount = allocation
                Log.d(TAG, "onCreateView: $it")
            }
            vm.allocations.value = tmp
//                allocationsRV.adapter?.notifyDataSetChanged()
            val allocationsRV = view.findViewById<RecyclerView>(R.id.rvPayees)
            allocationsRV.adapter = AllocationAdapter(tmp, vm)
        } else if (unlockedBalance < 0.0) {
            Toast.makeText(context, "Locked fragment_allocations cannot exceed amount paid", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        // TODO: Use the ViewModel
        (activity as AppCompatActivity).apply {
            setSupportActionBar(findViewById(R.id.toolbar))
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}