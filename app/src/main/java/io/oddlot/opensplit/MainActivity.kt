package io.oddlot.opensplit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.oddlot.opensplit.models.Group
import kotlinx.android.synthetic.main.fragment_expense.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val navController: NavController
        get() = findNavController(R.id.navHostFragment)

    private lateinit var bsb: BottomSheetBehavior<LinearLayoutCompat>
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = getString(R.string.home)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val bsd = findViewById<LinearLayoutCompat>(R.id.bottomSheet)
        val submitBtn = bsd.findViewById<MaterialButton>(R.id.btnSubmitGroup)

        bsb = BottomSheetBehavior.from(bsd)
        bsb.isGestureInsetBottomIgnored = false
        bsb.state = BottomSheetBehavior.STATE_HIDDEN

        val scrim = findViewById<View>(R.id.scrim)

        scrim.setOnTouchListener { view, motionEvent ->
            view.performClick()
            bsb.state = BottomSheetBehavior.STATE_HIDDEN

            true
        }

        bsb.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    scrim.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                scrim.visibility = View.VISIBLE
                scrim.background.alpha = 255 + (slideOffset * 255).toInt()
            }

        })

        submitBtn.setOnClickListener {
            val groupNameInput = bsd.findViewById<EditText>(R.id.newGroupName).text

            if (!groupNameInput.isNullOrBlank()) {
                val newGroup = Group(null, groupNameInput.toString())

                CoroutineScope(Dispatchers.IO).launch {
                    val groupId = Application.getDatabase(applicationContext).groupDao()
                        .insert(newGroup)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Group created: $groupNameInput",
                            Toast.LENGTH_SHORT
                        ).show()
                        groupNameInput.clear()
                    }
                }

                bsb.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        fab.setOnClickListener { fab ->
            bsb.state = BottomSheetBehavior.STATE_EXPANDED

            val navHostController = findNavController(R.id.navHostFragment)
//            bsd.getChildAt(0).setOnClickListener {
//                navHostController.navigate(R.id.action_homeFragment_to_groupFragment)
//                bsb.state = BottomSheetBehavior.STATE_HIDDEN
//            }
            bsd.getChildAt(0).setOnClickListener {
                navHostController.navigate(R.id.action_homeFragment_to_ticketFragment)
                bsb.state = BottomSheetBehavior.STATE_HIDDEN
                fab.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        if (bsb.state == BottomSheetBehavior.STATE_EXPANDED) {
            bsb.state = BottomSheetBehavior.STATE_HIDDEN
            Log.d(TAG, "onBackPressed: hiding bottom sheet")
        } else if (navController.currentDestination?.id == 2131231125) {
            Log.d(TAG, "onBackPressed: on ticket fragment")
            MaterialAlertDialogBuilder(this).apply {
                setTitle("You have unsaved changes")
                setMessage("Are you sure you would like to quit?")
                setPositiveButton("Yes") { dialog, which ->
                    super.onBackPressed()
                }
                setNegativeButton("No") { dialog, which ->
                    // DO NOTHING
                }
            }.show()
        } else {
            Log.d(TAG, "onBackPressed: else")
            super.onBackPressed()
        }
    }
}