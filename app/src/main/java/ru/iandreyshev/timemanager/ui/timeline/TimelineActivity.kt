package ru.iandreyshev.timemanager.ui.timeline

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_timeline.*
import kotlinx.android.synthetic.main.menu_bottom_dialog.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.ui.BaseActivity
import ru.iandreyshev.timemanager.ui.extensions.asTimerTitleViewState
import ru.iandreyshev.timemanager.ui.extensions.getCardTitle
import ru.iandreyshev.timemanager.utils.dismissOnDestroy

class TimelineActivity : BaseActivity() {

    private val mViewModel: TimelineViewModel by lazy { getViewModel() }
    private var mDeleteCardDialog: AlertDialog? = null
    private var mEventMenuPopup: PopupMenu? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        initButtons()
        initEventsList()
        subscribeToViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDeleteCardDialog.dismissOnDestroy()
        mEventMenuPopup?.dismissOnDestroy()
        mBottomSheetDialog?.dismissOnDestroy()
    }

    override fun onBackPressed() {
        if (!mViewModel.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun initButtons() {
        nextButton.setOnClickListener { mViewModel.onNextCard() }
        previousButton.setOnClickListener { mViewModel.onPreviousCard() }
        titleClickableArea.setOnClickListener { openBottomMenu() }
        createFirstCardButton.setOnClickListener { mViewModel.onCreateFirstCard() }
        nextCardButton.setOnClickListener { mViewModel.onCreateCard() }
        createEventButton.setOnClickListener { mViewModel.onCreateEvent() }
        createFirstEventButton.setOnClickListener { mViewModel.onCreateEvent() }
        exitFromTimerButtonClickableArea.setOnClickListener { mViewModel.onExitFromTimer() }
    }

    private fun initEventsList() {
        recyclerView.adapter = mViewModel.eventsAdapter
        mViewModel.eventsAdapter.onOptionsClick = { view, position ->
            mEventMenuPopup?.dismissOnDestroy()
            mEventMenuPopup = PopupMenu(this, view)
            mEventMenuPopup?.inflate(R.menu.menu_timeline_event)
            mEventMenuPopup?.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_timeline_event_delete -> {
                        mViewModel.onDeleteEventAt(position)
                        true
                    }
                    else -> false
                }
            }
            mEventMenuPopup?.show()
        }
    }

    private fun subscribeToViewModel() {
        mViewModel.cardTitleViewState.observe(::updateTitle)
        mViewModel.hasEventsList.observe {
            timelineFirstEventView.isGone = it
        }
        mViewModel.canAddEvent.observe {
            if (it) createEventButton.show() else createEventButton.hide()
        }
        mViewModel.nextCardButtonViewState.observe { nextCardButton.isVisible = it }
        mViewModel.timelineViewState.observe { viewState ->
            when (viewState) {
                TimelineViewState.EMPTY -> {
                    headerGroup.isVisible = false
                    timelineFirstCardView.isVisible = true
                    timelineLoadingView.isVisible = false
                }
                TimelineViewState.LOADING -> {
                    headerGroup.isVisible = false
                    timelineFirstCardView.isVisible = false
                    timelineLoadingView.isVisible = true
                }
                TimelineViewState.HAS_CARD -> {
                    headerGroup.isVisible = true
                    timelineFirstCardView.isVisible = false
                    timelineLoadingView.isVisible = false
                }
            }
        }
        mViewModel.arrowsViewState.observe {
            previousButton.isClickable = it.first == ArrowViewState.ARROW
            previousButtonIcon.isInvisible = it.first != ArrowViewState.ARROW

            nextButton.isClickable = it.second == ArrowViewState.ARROW
            nextButtonIcon.isInvisible = it.second == ArrowViewState.NEXT_CARD
                    || it.second == ArrowViewState.HIDDEN

            nextCardButton.isClickable = it.second == ArrowViewState.NEXT_CARD
            nextCardButton.isInvisible = it.second != ArrowViewState.NEXT_CARD
        }
        mViewModel.toolbarViewState.observe { viewState ->
            when (viewState) {
                is ToolbarViewState.CardTitle -> {
                    timerGroup.isVisible = false
                    cardTitleGroup.isVisible = true
                }
                is ToolbarViewState.Timer -> {
                    cardTitleGroup.isVisible = false
                    timerGroup.isVisible = true
                    timerTitle.text = viewState.minutes.asTimerTitleViewState(resources)
                }
            }
        }
    }

    private fun openBottomMenu() {
        mBottomSheetDialog.dismissOnDestroy()
        mBottomSheetDialog = BottomSheetDialog(this).apply {
            val view = layoutInflater.inflate(R.layout.menu_bottom_dialog, null).apply {
                bottomMenuGotoLastClickableArea?.setOnClickListener {
                    mBottomSheetDialog.dismissOnDestroy()
                    mViewModel.onResetToLast()
                }
                bottomMenuDeleteClickableArea?.setOnClickListener {
                    mBottomSheetDialog.dismissOnDestroy()
                    showDeleteCardDialog()
                }
            }

            setContentView(view)
            show()
        }
    }

    private fun showDeleteCardDialog(): Boolean {
        mDeleteCardDialog?.setOnDismissListener(null)
        mDeleteCardDialog?.dismiss()
        mDeleteCardDialog = alert(Appcompat) {
            titleResource = R.string.delete_card_dialog_title
            message = getString(
                R.string.delete_card_message,
                mViewModel.cardTitleViewState.value?.asTitle()
            )
            noButton { it.dismiss() }
            yesButton { mViewModel.onDeleteCard() }
        }.build()
        mDeleteCardDialog?.show()

        return true
    }

    private fun updateTitle(viewState: CardTitleViewState) {
        cardTitle.text = viewState.asTitle()
    }

    private fun CardTitleViewState.asTitle(): String {
        return resources.getCardTitle(
            dateTime ?: return "",
            repeatIndex ?: return ""
        )
    }

}
