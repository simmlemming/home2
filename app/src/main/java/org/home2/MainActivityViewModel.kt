package org.home2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

/**
 * Created by mtkachenko on 20/05/17.
 */
class MainActivityViewModel(val tempRepository: TempRepository) : ViewModel() {
    var temp : LiveData<Int> = tempRepository.getTemp()

    @Suppress("unused")
    constructor() : this(TempRepository())
}