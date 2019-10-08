package ru.iandreyshev.timemanager.ui.tutorial

import androidx.lifecycle.ViewModel
import ru.iandreyshev.timemanager.TimeCardsApp
import ru.iandreyshev.timemanager.domain.system.IAppRepository

class TutorialViewModel(
    private val repository: IAppRepository
) : ViewModel() {

    fun onTutorialCompleted() {
        repository.setFirstLaunch(true)
        TimeCardsApp.navigator.openCards()
    }

}
