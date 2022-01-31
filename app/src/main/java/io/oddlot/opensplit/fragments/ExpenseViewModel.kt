package io.oddlot.opensplit.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.oddlot.opensplit.models.Member
import java.util.*

class ExpenseViewModel : ViewModel() {
    var date: Date = Date()
    val amountPaid: MutableLiveData<Double> = MutableLiveData<Double>()
    var payer: String = "thomas"

    val allocations: MutableLiveData<MutableList<AllocationItem>> by lazy {
        MutableLiveData<MutableList<AllocationItem>>().apply {
            value = mutableListOf(AllocationItem("Thomas", 0.0), AllocationItem("Cecilia", 0.0))
        }
    }

    val recipients: MutableLiveData<List<Member>> by lazy {
        MutableLiveData<List<Member>>().apply {
            value = mutableListOf(Member(null, "Thomas"))
        }
    }

    class AllocationItem(val name: String, var amount: Double, var locked: Boolean = false) {
        override fun toString(): String {
            return "Name: $name, Amount: $amount, Locked: $locked"
        }
    }
}

class TicketViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            ExpenseViewModel() as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}