package ru.iandreyshev.timemanager

import android.os.Bundle
import ru.iandreyshev.timemanager.ui.BaseActivity

class AppActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            TimeCardsApp.launcher.onColdStart()
        }
    }

}
