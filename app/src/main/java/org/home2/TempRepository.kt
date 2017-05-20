package org.home2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

/**
 * Created by mtkachenko on 20/05/17.
 */
class TempRepository {

    fun getTemp(): LiveData<Int> {
        val data = MutableLiveData<Int>()

        val t = Thread {
            Thread.sleep(2000)
            data.postValue(30)
        }

        t.start()

        return data
    }
}