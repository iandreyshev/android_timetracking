package ru.iandreyshev.timemanager.navigation

import android.content.Context
import android.content.Intent
import org.jetbrains.anko.newTask
import ru.iandreyshev.timemanager.domain.cards.CardId
import ru.iandreyshev.timemanager.domain.cards.EventId
import ru.iandreyshev.timemanager.ui.editor.EditorActivity
import ru.iandreyshev.timemanager.ui.timeline.TimelineActivity
import ru.iandreyshev.timemanager.ui.tutorial.TutorialActivity

class Navigator(
    private val applicationContext: Context
) {

    fun openTutorial() {
        Intent(applicationContext, TutorialActivity::class.java)
            .newTask()
            .let(applicationContext::startActivity)
    }

    fun openCards() {
        Intent(applicationContext, TimelineActivity::class.java)
            .newTask()
            .let(applicationContext::startActivity)
    }

    fun openEditor(cardId: CardId) =
        openEditor(cardId, EventId.default())

    fun openEditor(cardId: CardId, eventId: EventId) {
        Intent(applicationContext, EditorActivity::class.java)
            .newTask()
            .apply {
                putExtra(EditorActivity.ARG_CARD_ID, cardId.value)
                putExtra(EditorActivity.ARG_EVENT_ID, eventId.value)
            }
            .let(applicationContext::startActivity)
    }

}
