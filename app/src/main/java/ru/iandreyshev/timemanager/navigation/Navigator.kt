package ru.iandreyshev.timemanager.navigation

import android.content.Context
import org.jetbrains.anko.startActivity
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.ui.editor.EditorActivity

class Navigator(
    private val applicationContext: Context
) {

    fun openEditor(dateTime: ZonedDateTime, eventToEdit: Event?) {
        applicationContext.startActivity<EditorActivity>()
    }

}
