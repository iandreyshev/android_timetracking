package ru.iandreyshev.timemanager

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class TimeManagerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

}