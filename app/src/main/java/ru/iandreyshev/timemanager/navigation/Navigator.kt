package ru.iandreyshev.timemanager.navigation

import android.content.Context
import org.jetbrains.anko.startActivity
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.Card
import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.domain.EventId
import ru.iandreyshev.timemanager.ui.editor.EditorActivity

class Navigator(
    private val applicationContext: Context
) {

    fun openEditor(card: Card, eventToEdit: EventId?) {
        applicationContext.startActivity<EditorActivity>()
    }

}
