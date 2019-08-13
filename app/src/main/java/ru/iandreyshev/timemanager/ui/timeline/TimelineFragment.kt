package ru.iandreyshev.timemanager.ui.timeline

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_timeline.*
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.ui.BaseFragment

class TimelineFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_timeline

    private val mViewModel: TimelineViewModel by lazy { getViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
        subscribeToViewModel()
    }

    private fun initButtons() {
        nextButton.setOnClickListener { mViewModel.onNextDate() }
        previousButton.setOnClickListener { mViewModel.onPreviousDate() }
        dateTitleClickableArea.setOnClickListener { mViewModel.onResetToCurrent() }
        dateTitleClickableArea.setOnLongClickListener { mViewModel.onOpenDatePicker() }
        createEventButton.setOnClickListener { mViewModel.onCreateEvent() }
    }

    private fun subscribeToViewModel() {
        mViewModel.dateViewState.observe(viewLifecycleOwner, Observer { viewState ->
            dateTitle.text = viewState
        })
    }

    companion object {
        fun newInstance() = TimelineFragment()
    }

}
