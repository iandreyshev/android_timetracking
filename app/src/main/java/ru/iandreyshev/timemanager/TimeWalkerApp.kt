package ru.iandreyshev.timemanager

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import ru.iandreyshev.timemanager.domain.DateProvider
import ru.iandreyshev.timemanager.domain.IDateProvider
import ru.iandreyshev.timemanager.domain.IRepository
import ru.iandreyshev.timemanager.navigation.Navigator
import ru.iandreyshev.timemanager.repository.AppDatabase
import ru.iandreyshev.timemanager.repository.Repository
import ru.iandreyshev.timemanager.ui.editor.EditorAction
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

        val repository: IRepository by lazy {
            Repository(
                cardDao = mDatabase.cardDao(),
                eventDao = mDatabase.eventDao()
            )
        }

        val navigator by lazy { Navigator(applicationContext) }

        val editorObserver: Observer<EditorAction> by lazy { mEditorSubject }
        val editorObservable: Observable<EditorAction> by lazy { mEditorSubject }

        private val mDatabase by lazy {
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "database"
            ).build()
        }
        private val mEditorSubject by lazy { BehaviorSubject.create<EditorAction>() }
    }

}
