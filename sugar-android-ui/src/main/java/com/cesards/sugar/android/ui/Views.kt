package com.cesards.sugar.android.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun <T : ViewDataBinding?> View.setLayoutBinding(layoutId: Int, container: ViewGroup?): T {
  return DataBindingUtil.inflate<T>(LayoutInflater.from(context), layoutId, container, false)
}
