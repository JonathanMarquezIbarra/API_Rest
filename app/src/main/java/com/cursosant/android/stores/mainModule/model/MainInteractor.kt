package com.cursosant.android.stores.mainModule.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.cursosant.android.stores.StoreApplication
import com.cursosant.android.stores.common.entities.StoreEntity
import com.cursosant.android.stores.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

/****
 * Project: Stores
 * From: com.cursosant.android.stores.mainModule.model
 * Created by Alain Nicolás Tello on 2/4/21 at 12:06 PM
 * Course: Android Practical with Kotlin from zero.
 * Only on: https://www.udemy.com/course/kotlin-intensivo/
 * All rights reserved 2021.
 *
 * All my Courses(Only on Udemy):
 * https://www.udemy.com/user/alain-nicolas-tello/
 ***/
class MainInteractor {

    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
        val url = Constants.STORES_URL + Constants.GET_ALL_PATH

        var storeList = mutableListOf<StoreEntity>()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->

            //val status = response.getInt(Constants.STATUS_PROPERTY)
            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)
            Log.i("status", status.toString())

            if (status == Constants.SUCCESS){

                val jsonList = response.getJSONArray(Constants.STORES_PROPERTY)?.toString()
                if (jsonList != null) {
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>() {}.type
                    storeList = Gson().fromJson(jsonList, mutableListType)

                    callback(storeList)
                    return@JsonObjectRequest
                }
            }
            callback(storeList)

        }, {
            it.printStackTrace()
            callback(storeList)
        })
        StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)
    }

    fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit){
        doAsync {
            val storeList = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                val json = Gson().toJson(storeList)
                Log.i("Gson", json)
                callback(storeList)
            }
        }
    }

    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }

    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }
}