package ru.iandreyshev.timemanager.ui.tutorial

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.annotation.StringRes
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlinx.android.synthetic.main.item_timeline_event.view.*
import org.jetbrains.anko.textResource
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.ui.BaseActivity
import timber.log.Timber

class TutorialActivity : BaseActivity() {

    private val mViewModel: TutorialViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        event1.title.text = getString(R.string.tutorial_event_1_title)
        event2.title.text = getString(R.string.tutorial_event_2_title)
        event3.title.text = getString(R.string.tutorial_event_3_title)

        tutorialDoneButton.setOnClickListener {
            mViewModel.onTutorialCompleted()
        }

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
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
                Timber.d("onTransitionChange startId:$startId endId:$endId progress:$progress")
            }

            override fun onTransitionCompleted(layout: MotionLayout?, id: Int) {
                Timber.d("onTransitionCompleted id:$id")
            }
        })

    }

    private fun setStateText(@StringRes textRes: Int) {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatCount = 1
        anim.repeatMode = Animation.REVERSE

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {
                stateDescription.textResource = textRes
            }
        })

        stateDescription.startAnimation(anim)
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
