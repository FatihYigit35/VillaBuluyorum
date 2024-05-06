package com.androiddevelopers.villabuluyorum.util

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.androiddevelopers.villabuluyorum.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun hideBottomNavigation(act: FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.nav_view)
    bottomNavigationView?.visibility = View.GONE
}

fun showBottomNavigation(act: FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.nav_view)
    bottomNavigationView?.visibility = View.VISIBLE
}

fun startLoadingProcess(progressDialog: ProgressDialog?) {
    progressDialog?.setMessage("Bilgiler güncelleniyor...")
    progressDialog?.setCancelable(false)
    progressDialog?.show()
}

fun hideHostBottomNavigation(act: FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.host_nav_view)
    bottomNavigationView?.visibility = View.GONE
}

fun showHostBottomNavigation(act: FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.host_nav_view)
    bottomNavigationView?.visibility = View.VISIBLE
}

fun setupDialogs(errorDialog: AlertDialog, activity: Activity? = null) {
    with(errorDialog) {
        setTitle("Bilgiler alınırken sorun oluştu.")
        setCancelable(false)
        setButton(
            AlertDialog.BUTTON_POSITIVE, "Tamam"
        ) { dialog, _ ->
            activity?.finish()
            dialog.cancel()
        }
    }
}