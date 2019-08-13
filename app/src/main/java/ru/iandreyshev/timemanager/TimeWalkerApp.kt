package ru.iandreyshev.timemanager

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.iandreyshev.timemanager.domain.DateProvider
import ru.iandreyshev.timemanager.domain.EventsRepo
import ru.iandreyshev.timemanager.domain.IDateProvider
import ru.iandreyshev.timemanager.domain.IEventsRepo
import ru.iandreyshev.timemanager.navigation.Navigator

class TimeWalkerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        TimeWalkerApp.applicationContext = this
    }

    companion object {
        lateinit var applicationContext: Context
            private set
        val dateProvider: IDateProvider by lazy { DateProvider() }
        val eventsRepo: IEventsRepo by lazy { EventsRepo() }
        val navigator by lazy { Navigator(applicationContext) }
    }

}
