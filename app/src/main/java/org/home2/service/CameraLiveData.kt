package org.home2.service

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.home2.NetworkResource
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CameraLiveData(private val deviceName: String, private val cameraIndex: Int) : MutableLiveData<NetworkResource<CameraLiveData.CameraDeviceInfo>>() {
    private val httpClient: OkHttpClient

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
    }

    fun refreshPicture(timestamp: Date) {
        val request = Request.Builder()
                .url("http://myhome.im:8080/p?timestamp=${DATE_FORMAT.format(timestamp)}&camera_index=$cameraIndex")
                .build()

        value = NetworkResource.loading(null)

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                postValue(NetworkResource.error(IOException(e?.message)))
            }

            override fun onResponse(call: Call?, response: Response) {
                val bytes = response.body()?.byteStream()
                val picture = if (bytes != null) {
                    BitmapFactory.decodeStream(bytes)
                } else null

                if (picture == null) {
                    postValue(NetworkResource.error(IOException("Cannot decode picture")))
                } else {
                    postValue(NetworkResource.success(CameraDeviceInfo(deviceName, picture)))
                }
            }
        })
    }

    class CameraDeviceInfo(val name: String, val picture: Bitmap)

    companion object {
        @SuppressLint("ConstantLocale")
        private val DATE_FORMAT = SimpleDateFormat("yyyy-mm-dd-HH-MM-SS", Locale.getDefault())
    }
}