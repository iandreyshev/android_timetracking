package ru.iandreyshev.timemanager.repository.system

import android.content.Context
import ru.iandreyshev.timemanager.domain.system.IAppRepository

class AppRepository(
    private val appContext: Context
) : IAppRepository {

    private val mPreferences by lazy {
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun isFirstLaunchCompleted() =
        mPreferences.getBoolean(PREFS_KEY_FIRST_LAUNCH_COMPLETED, false)

    override fun setFirstLaunch(isCompleted: Boolean) {
        mPreferences.edit()
            .putBoolean(PREFS_KEY_FIRST_LAUNCH_COMPLETED, isCompleted)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val PREFS_KEY_FIRST_LAUNCH_COMPLETED = "prefs_key_first_launch"
    }

}
