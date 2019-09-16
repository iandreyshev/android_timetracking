package ru.iandreyshev.timemanager.navigation

import android.content.Context
import android.content.Intent
import org.jetbrains.anko.newTask
import ru.iandreyshev.timemanager.domain.CardId
import ru.iandreyshev.timemanager.domain.EventId
import ru.iandreyshev.timemanager.ui.editor.EditorActivity

class Navigator(
    private val applicationContext: Context
) {

    fun openEditor(cardId: CardId) =
        openEditor(cardId, EventId.default())

    fun openEditor(cardId: CardId, eventId: EventId) {
        val intent = Intent(applicationContext, EditorActivity::class.java)
            .newTask()
            .apply {
                putExtra(EditorActivity.ARG_CARD_ID, cardId.value)
                putExtra(EditorActivity.ARG_EVENT_ID, eventId?.value)
            }
        applicationContext.startActivity(intent)
    }

}
