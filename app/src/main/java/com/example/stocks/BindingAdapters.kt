/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.stocks

import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("getColor")
fun getColor(view: View, isColored: Boolean) {
    val typedValueColor = TypedValue()
    val ticker = view.findViewById<TextView>(R.id.ticker)
    val description = view.findViewById<TextView>(R.id.companySymbol)
    val current = view.findViewById<TextView>(R.id.curPrice)
    view.context.theme.resolveAttribute(R.attr.colorSurface, typedValueColor, true)
    if (!isColored) {
        view.context.theme.resolveAttribute(R.attr.colorSurface, typedValueColor, true)
        view.setBackgroundColor(typedValueColor.data)
        view.context.theme.resolveAttribute(R.attr.colorOnSurface, typedValueColor, true)
        ticker.setTextColor(typedValueColor.data)
        description.setTextColor(typedValueColor.data)
        current.setTextColor(typedValueColor.data)
    } else {
        view.context.theme.resolveAttribute(R.attr.colorSecondary, typedValueColor, true)
        view.setBackgroundColor(typedValueColor.data)
        view.context.theme.resolveAttribute(R.attr.colorOnSecondary, typedValueColor, true)
        ticker.setTextColor(typedValueColor.data)
        description.setTextColor(typedValueColor.data)
        current.setTextColor(typedValueColor.data)
    }
}
@BindingAdapter("setDifferenceColor")
fun setDifferenceColor(view: TextView, isColored: Boolean) {
    if (isColored)
        view.setTextColor(view.resources.getColor(R.color.red))
    else
        view.setTextColor(view.resources.getColor(R.color.green))

}