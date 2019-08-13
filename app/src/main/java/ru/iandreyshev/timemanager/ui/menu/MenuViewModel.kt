package ru.iandreyshev.timemanager.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MenuViewModel : ViewModel() {

    val menuItem: LiveData<MainMenuItem>
        get() = mMainMenuItem

    private val mMainMenuItem = MutableLiveData<MainMenuItem>().apply { value = MainMenuItem.TIMER }

}
