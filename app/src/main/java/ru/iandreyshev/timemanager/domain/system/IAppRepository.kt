package ru.iandreyshev.timemanager.domain.system

interface IAppRepository {
    fun isFirstLaunchCompleted(): Boolean
    fun setFirstLaunch(isCompleted: Boolean)
}
