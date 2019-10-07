package ru.iandreyshev.timemanager

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import ru.iandreyshev.timemanager.domain.cards.DateProvider
import ru.iandreyshev.timemanager.domain.cards.IDateProvider
import ru.iandreyshev.timemanager.domain.cards.ICardsRepository
import ru.iandreyshev.timemanager.domain.system.AppLauncher
import ru.iandreyshev.timemanager.domain.system.IAppRepository
import ru.iandreyshev.timemanager.navigation.Navigator
import ru.iandreyshev.timemanager.repository.cards.AppDatabase
import ru.iandreyshev.timemanager.repository.cards.CardsRepository
import ru.iandreyshev.timemanager.repository.system.AppRepository
import ru.iandreyshev.timemanager.ui.editor.EditorAction
import timber.log.Timber

class TimeCardsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        TimeCardsApp.applicationContext = this
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var applicationContext: Context
            private set

        val dateProvider: IDateProvider by lazy { DateProvider() }

        val appRepository: IAppRepository by lazy { AppRepository(applicationContext) }

        val cardsRepository: ICardsRepository by lazy {
            CardsRepository(
                cardDao = mDatabase.cardDao(),
                eventDao = mDatabase.eventDao()
            )
        }

        val navigator by lazy { Navigator(applicationContext) }

        val editorObserver: Observer<EditorAction> by lazy { mEditorSubject }
        val editorObservable: Observable<EditorAction> by lazy { mEditorSubject }

        val launcher by lazy { AppLauncher(repository = appRepository) }

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
