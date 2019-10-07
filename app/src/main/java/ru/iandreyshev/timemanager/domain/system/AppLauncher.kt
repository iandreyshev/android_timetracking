package ru.iandreyshev.timemanager.domain.system

import ru.iandreyshev.timemanager.TimeCardsApp

class AppLauncher(
    private val repository: IAppRepository
) {

    fun onColdStart() {
        if (!repository.isFirstLaunchCompleted()) {
            TimeCardsApp.navigator.openTutorial()
        } else {
            TimeCardsApp.navigator.openCards()
        }
    }

}
