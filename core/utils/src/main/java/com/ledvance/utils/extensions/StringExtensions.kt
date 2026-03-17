package com.ledvance.utils.extensions

import androidx.annotation.StringRes
import com.ledvance.utils.AppContext

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/13/25 15:40
 * Describe : StringExtensions
 */
fun getString(@StringRes resId: Int) = AppContext.get().getString(resId)