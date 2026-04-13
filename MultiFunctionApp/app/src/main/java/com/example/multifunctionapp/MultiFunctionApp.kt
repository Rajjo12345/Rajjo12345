package com.example.multifunctionapp

import android.app.Application
import androidx.room.Room

class MultiFunctionApp : Application() {
    
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "multifunction_database"
        ).build()
    }
}
