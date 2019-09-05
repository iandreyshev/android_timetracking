package ru.iandreyshev.timemanager

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.iandreyshev.timemanager.domain.DateProvider
import ru.iandreyshev.timemanager.domain.IDateProvider
import ru.iandreyshev.timemanager.domain.IEventsRepo
import ru.iandreyshev.timemanager.navigation.Navigator
import ru.iandreyshev.timemanager.repository.AppDatabase
import ru.iandreyshev.timemanager.repository.EventsRepo
import timber.log.Timber

class TimeWalkerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        TimeWalkerApp.applicationContext = this
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var applicationContext: Context
            private set
        val dateProvider: IDateProvider by lazy { DateProvider() }
        val eventsRepo: IEventsRepo by lazy {
            EventsRepo(
                cardDao = database.cardDao(),
                eventDao = database.eventDao()
            )
        }
        val navigator by lazy { Navigator(applicationContext) }

        private val database by lazy {
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "database"
            ).build()
        }
    }

}
