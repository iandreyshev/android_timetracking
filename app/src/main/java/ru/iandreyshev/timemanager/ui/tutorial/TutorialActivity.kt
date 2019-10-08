package ru.iandreyshev.timemanager.ui.tutorial

import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlinx.android.synthetic.main.item_timeline_event.view.*
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

//        mViewModel.cardVisible.observeVisibility(card)
//        mViewModel.timerVisible.observeVisibility(timerGroupTitle)
//        mViewModel.firstCardButtonVisible.observeVisibility(createFirstCardButton)
//        mViewModel.firstEventButtonVisible.observeVisibility(createFirstEventButton)
//        mViewModel.createEventButtonVisible.observeVisibility(createEventButton)
//        mViewModel.event1Visible.observeVisibility(event1)
//        mViewModel.event2Visible.observeVisibility(event2)
//        mViewModel.event3Visible.observeVisibility(event3)
//        mViewModel.doneButtonVisible.observeVisibility(tutorialDoneButton)

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(layout: MotionLayout?, id: Int, p2: Boolean, p3: Float) {
                Timber.d("onTransitionTrigger id:$id p2:$p2 p3:$p3")
            }

            override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {
                Timber.d("onTransitionStarted startId:$startId endId:$endId")
            }

            override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                Timber.d("onTransitionChange startId:$startId endId:$endId progress:$progress")
            }

            override fun onTransitionCompleted(layout: MotionLayout?, id: Int) {
                Timber.d("onTransitionCompleted id:$id")
            }
        })

    }

}
