package com.example.deep

import android.content.Context
import androidx.core.net.toUri
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SongRepository {
    val songs: MutableStateFlow<List<Song>> = MutableStateFlow<List<Song>>(emptyList())
    val depths: MutableStateFlow<List<Depth>> = MutableStateFlow(listOf(Depth("ADD DEPTH", song_catalog =  emptyList(), depth_id = -1 , "sss"   ,"")))
    var counter =MutableStateFlow(0)
    fun saveData(context: Context, thesongs:List<Song>, thedepth:List<Depth> ){

            val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val gson = Gson()
            var saveddata=SaveData(thesongs,thedepth)

            var json = gson.toJson(saveddata) // Convert object to JSON
            editor.putString("savedataa1", json)
            editor.apply()
            songs.value=thesongs
            depths.value=thedepth
        }
     fun returnData(thecon:Context):SaveData {
        val sharedPref = thecon.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("savedataa1", null)

        if (json == null) {
            return SaveData(
                emptyList(),
                listOf(Depth("ADD DEPTH", song_catalog = emptyList(), depth_id = -1, "sss",""))
            )


        } else {
           return gson.fromJson(json, SaveData::class.java) // Convert JSON to Object

        }


    }

    private val _currentIndex = MutableStateFlow(0)
    var currentIndex: MutableStateFlow<Int> = _currentIndex
    var iscatalog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var catalog_songs: MutableStateFlow<List<Song>> = MutableStateFlow<List<Song>>(emptyList())

    fun setServicetoCatlaog(list:List<Song>){
        catalog_songs= MutableStateFlow(list)
        iscatalog=MutableStateFlow(true)


    }

}