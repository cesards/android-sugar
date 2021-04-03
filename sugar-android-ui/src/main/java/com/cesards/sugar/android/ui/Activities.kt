package com.cesards.sugar.android.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding

fun <T : ViewDataBinding?> AppCompatActivity.setActBinding(layoutId: Int): T {
  return DataBindingUtil.setContentView<T>(this, layoutId)
}
