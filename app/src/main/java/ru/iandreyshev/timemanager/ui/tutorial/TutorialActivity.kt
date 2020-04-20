package ru.iandreyshev.timemanager.ui.tutorial

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.annotation.StringRes
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_tutorial.*
import org.jetbrains.anko.textResource
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.ui.BaseActivity
import ru.iandreyshev.timemanager.ui.timeline.EventViewHolder
import timber.log.Timber

class TutorialActivity : BaseActivity() {

    private val mViewModel: TutorialViewModel by lazy { getViewModel() }

    private val mEvent1ViewHolder by lazy {
        EventViewHolder(
            event1,
            onClickListener = { mViewModel.onSecondEventSelected(TutorialEvent.EVENT_1) },
            onLongClickListener = { mViewModel.onFirstEventSelected(TutorialEvent.EVENT_1) },
            onOptionsClick = {})
    }
    private val mEvent2ViewHolder by lazy {
        EventViewHolder(
            event2,
            onClickListener = { mViewModel.onSecondEventSelected(TutorialEvent.EVENT_2) },
            onLongClickListener = { mViewModel.onFirstEventSelected(TutorialEvent.EVENT_2) },
            onOptionsClick = {})
    }
    private val mEvent3ViewHolder by lazy {
        EventViewHolder(
            event3,
            onClickListener = { mViewModel.onSecondEventSelected(TutorialEvent.EVENT_3) },
            onLongClickListener = { mViewModel.onFirstEventSelected(TutorialEvent.EVENT_3) },
            onOptionsClick = {})
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        mViewModel.startTransitionEvent.consume {
            Timber.d("Start transition from view model: ${it.name}")
            motionLayout.transitionToState(it.id)
        }
        mViewModel.event1ViewState.observe(mEvent1ViewHolder::bind)
        mViewModel.event2ViewState.observe(mEvent2ViewHolder::bind)
        mViewModel.event3ViewState.observe(mEvent3ViewHolder::bind)
        mViewModel.timerViewState.observe(timerTitle::setText)

        createFirstCardButton.setOnClickListener {
            motionLayout.transitionToState(TutorialState.EMPTY_CARD.id)
        }
        tutorialDoneButton.setOnClickListener {
            mViewModel.onTutorialCompleted()
        }

        motionLayout.setTransitionListener(TransitionListener())
    }

    private fun setStateText(@StringRes textRes: Int) {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatCount = 1
        anim.repeatMode = Animation.REVERSE
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) = Unit
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationRepeat(animation: Animation?) {
                stateDescription.textResource = textRes
            }
        })

        stateDescription.startAnimation(anim)
    }

    private inner class TransitionListener : MotionLayout.TransitionListener {
        override fun onTransitionTrigger(
            layout: MotionLayout?,
            id: Int,
            p2: Boolean,
            p3: Float
        ) {
            Timber.d("onTransitionTrigger id:$id p2:$p2 p3:$p3")
        }

        override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {
            Timber.d("onTransitionStarted startId:${startId.asState()} endId:${endId.asState()}")

            val nextState = endId.asState()

            if (nextState.needChangeDescription()) {
                setStateText(nextState.getDescriptionRes())
            }
        }

        override fun onTransitionChange(
            layout: MotionLayout?,
            startId: Int,
            endId: Int,
            progress: Float
        ) {
            Timber.d("onTransitionChange start:${startId.asState()} endId:${endId.asState()} progress:$progress")
        }

        override fun onTransitionCompleted(layout: MotionLayout?, id: Int) {
            Timber.d("onTransitionCompleted id:$id")
        }
    }

    private fun TutorialState.getDescriptionRes() = when (this) {
        TutorialState.UNDEFINED,
        TutorialState.TWO_EVENTS,
        TutorialState.TWO_EVENTS_PAUSE,
        TutorialState.AWAIT_DONE,
        TutorialState.DONE -> R.string.empty
        TutorialState.NO_CARD -> R.string.tutorial_state_no_card
        TutorialState.EMPTY_CARD -> R.string.tutorial_state_empty_card
        TutorialState.ONE_EVENT -> R.string.tutorial_state_one_event
        TutorialState.THREE_EVENTS -> R.string.tutorial_state_three_events
        TutorialState.ONE_EVENT_SELECTED -> R.string.tutorial_state_one_event_selected
        TutorialState.TWO_EVENTS_SELECTED -> R.string.tutorial_state_two_events_selected
    }

    private fun TutorialState.needChangeDescription() = when (this) {
        TutorialState.UNDEFINED,
        TutorialState.TWO_EVENTS,
        TutorialState.TWO_EVENTS_PAUSE,
        TutorialState.AWAIT_DONE,
        TutorialState.DONE -> false
        else -> true
    }

    private fun Int.asState() = TutorialState.from(this)

}
