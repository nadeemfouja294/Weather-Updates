package com.logical.weatherupdate.utilities

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

fun Context.hideKeyboard(activity: Activity){
    val inputMethodManager=activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView=activity.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}
fun Context.toast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()

}