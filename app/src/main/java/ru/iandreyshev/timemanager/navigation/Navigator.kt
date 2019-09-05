package ru.iandreyshev.timemanager.navigation

import android.content.Context
import org.jetbrains.anko.startActivity
import ru.iandreyshev.timemanager.domain.CardId
import ru.iandreyshev.timemanager.domain.EventId
import ru.iandreyshev.timemanager.ui.editor.EditorActivity

class Navigator(
    private val applicationContext: Context
) {

    fun openEditor(cardId: CardId, eventId: EventId) {
        applicationContext.startActivity<EditorActivity>(
            EditorActivity.ARG_CARD_ID to cardId.value,
            EditorActivity.ARG_EVENT_ID to eventId.value
        )
    }

}
