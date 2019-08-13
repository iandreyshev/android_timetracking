package ru.iandreyshev.timemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.iandreyshev.timemanager.ui.menu.MenuFragment
import ru.iandreyshev.timemanager.ui.timer.TimerFragment

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_container)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, TimerFragment.newInstance(), "menu_tag")
                .addToBackStack("menu_tag")
                .commit()
        }
    }

}
