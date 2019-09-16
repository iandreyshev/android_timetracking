package ru.iandreyshev.timemanager.ui.editor

import ru.iandreyshev.timemanager.domain.CardId

sealed class EditorAction {
    class EditCompleted(val cardId: CardId) : EditorAction()
}
