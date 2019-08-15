package ru.iandreyshev.timemanager.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

abstract class BaseActivity : AppCompatActivity() {

    protected fun <T : Any> LiveData<T>.observe(observer: (T) -> Unit) {
        observe(this@BaseActivity, Observer { if (it != null) observer(it) })
    }

    protected fun <T : Any> LiveData<T>.observeNullable(observer: (T?) -> Unit) {
        observe(this@BaseActivity, Observer(observer))
    }

}