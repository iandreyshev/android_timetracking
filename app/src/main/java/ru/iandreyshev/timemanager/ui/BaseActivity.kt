package ru.iandreyshev.timemanager.ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import ru.iandreyshev.timemanager.ui.utils.LiveDataEvent

abstract class BaseActivity : AppCompatActivity() {

    protected fun <T : Any> LiveData<T>.observe(observer: (T) -> Unit) {
        observe(this@BaseActivity, Observer { if (it != null) observer(it) })
    }

    protected fun <T : Any> LiveData<T>.observeNullable(observer: (T?) -> Unit) {
        observe(this@BaseActivity, Observer(observer))
    }

    protected fun <T : Any> LiveData<LiveDataEvent<T>>.consume(observer: (T) -> Unit) {
        observe(
            this@BaseActivity,
            Observer { liveDataEvent -> liveDataEvent?.consume { value -> observer(value) } })
    }

    protected fun LiveData<Boolean>.observeVisibility(view: View) {
        observe { view.isVisible = it }
    }

}