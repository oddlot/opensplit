package io.oddlot.opensplit.adapters

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import io.oddlot.opensplit.OnSwipeTouchListener
import io.oddlot.opensplit.R
import io.oddlot.opensplit.round
import io.oddlot.opensplit.fragments.ExpenseViewModel

private const val TAG = "AllocationAdapter"
class AllocationAdapter(val allocationList: MutableList<ExpenseViewModel.AllocationItem>, val vm: ExpenseViewModel) : RecyclerView.Adapter<AllocationAdapter.PayeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.allocation_row, parent, false)

        return PayeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PayeeViewHolder, position: Int) {
        val item = allocationList[position]

        val payeeName = item.name
        val nameView = holder.view.findViewById<TextView>(R.id.payeeName)
            nameView.text = payeeName

        val amountEdit =  holder.view.findViewById<EditText>(R.id.allocationAmount)
        val amount = allocationList[position].amount

        val lockItemBtn = holder.view.findViewById<MaterialButton>(R.id.itemLockBtn).also {
            it.isChecked = item.locked
            it.setOnClickListener {
                item.locked = !item.locked
                amountEdit.isEnabled = !item.locked
            }
        }

        amountEdit.text = SpannableStringBuilder(if (amount == 0.0) "" else amount.round(2).toString())
        amountEdit.isEnabled = !lockItemBtn.isChecked
        amountEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                amountText: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val tmp = vm.allocations.value!!
                val field = amountText.toString()

                tmp[position].amount = if (field.isBlank() || field == (".")) {
                    0.0
                } else {
                    field.toDouble()
                }

                vm.allocations.value = tmp
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        amountEdit.setOnTouchListener(object : OnSwipeTouchListener(holder.view.context) {
            override fun onSwipeRight() {
                allocationList[position].amount = 0.0
                amountEdit.text = SpannableStringBuilder("0.0")
            }
        })
    }

    override fun getItemCount() = allocationList.size

    class PayeeViewHolder(val view: View): RecyclerView.ViewHolder(view)
}