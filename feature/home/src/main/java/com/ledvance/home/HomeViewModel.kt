package com.ledvance.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : HomeViewModel
 */
@HiltViewModel
internal class HomeViewModel @Inject constructor() : ViewModel(), HomeContract {
}