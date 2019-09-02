package ru.iandreyshev.timemanager.ui.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

typealias SingleLiveEvent<T> = LiveData<LiveDataEvent<T>>

class LiveDataEvent<T>(private val value: T) {

    private var mIsCompleted: Boolean = false

    fun consume(action: (T) -> Unit) {
        if (!mIsCompleted) {
            action(value)
            mIsCompleted = true
        }
    }

}

fun MutableLiveData<LiveDataEvent<Unit>>.execute() = setValue(LiveDataEvent(Unit))
fun <T> MutableLiveData<LiveDataEvent<T>>.execute(value: T) = setValue(LiveDataEvent(value))
