package ru.iandreyshev.timemanager

import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_timeline.*
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.ui.BaseActivity
import ru.iandreyshev.timemanager.ui.timeline.ArrowViewState
import ru.iandreyshev.timemanager.ui.timeline.TimelineViewModel
import ru.iandreyshev.timemanager.ui.timeline.TimelineViewState

class AppActivity : BaseActivity() {

    private val mViewModel: TimelineViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_timeline)

        initButtons()
        initEventsList()
        subscribeToViewModel()
    }

    private fun initButtons() {
        nextButton.setOnClickListener { mViewModel.onNextDate() }
        previousButton.setOnClickListener { mViewModel.onPreviousDate() }
        titleClickableArea.setOnClickListener { mViewModel.onResetToCurrent() }
        titleClickableArea.setOnLongClickListener { mViewModel.onOpenDatePicker() }
        createFirstCardButton.setOnClickListener { mViewModel.onCreateFirstCard() }
        nextCardButton.setOnClickListener { mViewModel.onCreateCard() }
        createEventButton.setOnClickListener { mViewModel.onCreateEvent() }
        createFirstEventButton.setOnClickListener { mViewModel.onCreateEvent() }
    }

    private fun initEventsList() {
        recyclerView.adapter = mViewModel.eventsAdapter
    }

    private fun subscribeToViewModel() {
        mViewModel.cardTitleViewState.observe(dateTitle::setText)
        mViewModel.hasEventsList.observe {
            timelineFirstEventView.isGone = it
            createEventButton.isVisible = it
        }
        mViewModel.nextCardButtonViewState.observe { nextCardButton.isVisible = it }
        mViewModel.timelineViewState.observe { viewState ->
            when (viewState) {
                TimelineViewState.EMPTY -> {
                    appBarLayout.isVisible = false
                    timelineFirstCardView.isVisible = true
                    timelineLoadingView.isVisible = false
                }
                TimelineViewState.LOADING -> {
                    appBarLayout.isVisible = false
                    timelineFirstCardView.isVisible = false
                    timelineLoadingView.isVisible = true
                }
                TimelineViewState.HAS_CARD -> {
                    appBarLayout.isVisible = true
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
    }

}
