package ru.iandreyshev.timemanager

import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_timeline.*
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.ui.BaseActivity
import ru.iandreyshev.timemanager.ui.timeline.TimelineViewModel
import ru.iandreyshev.timemanager.ui.timeline.TimelineViewState

class AppActivity : BaseActivity() {

    private val mViewModel: TimelineViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_timeline)

        initButtons()
        subscribeToViewModel()
    }

    private fun initButtons() {
        nextButton.setOnClickListener { mViewModel.onNextDate() }
        previousButton.setOnClickListener { mViewModel.onPreviousDate() }
        titleClickableArea.setOnClickListener { mViewModel.onResetToCurrent() }
        titleClickableArea.setOnLongClickListener { mViewModel.onOpenDatePicker() }
        createFirstCardButton.setOnClickListener { mViewModel.onCreateFirstCard() }
        createEventButton.setOnClickListener { mViewModel.onCreateEvent() }
        createFirstEventButton.setOnClickListener { mViewModel.onCreateEvent() }
    }

    private fun subscribeToViewModel() {
        mViewModel.cardTitleViewState.observe(dateTitle::setText)
        mViewModel.hasEventsList.observe { timelineFirstEventView.isVisible = !it }
        mViewModel.timelineViewState.observe {
            when (it) {
                TimelineViewState.EMPTY -> {
                    appBarLayout.isVisible = false
                    timelineFirstCardView.isVisible = true
                    timelineLoadingView.isVisible = false
                    createEventButton.isVisible = false
                }
                TimelineViewState.PRELOADER -> {
                    appBarLayout.isVisible = false
                    timelineFirstCardView.isVisible = false
                    timelineLoadingView.isVisible = true
                    createEventButton.isVisible = false
                }
                TimelineViewState.TIMELINE -> {
                    appBarLayout.isVisible = true
                    timelineFirstCardView.isVisible = false
                    timelineLoadingView.isVisible = false
                    createEventButton.isVisible = true
                }
            }
        }
        mViewModel.arrowsViewState.observe {
            previousButton.isClickable = it.first
            previousButtonIcon.isInvisible = !it.first
            nextButton.isClickable = it.second
            nextButtonIcon.isInvisible = !it.second
        }
    }

}
