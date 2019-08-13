package ru.iandreyshev.timemanager.ui.timer

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_timer.*
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.ui.BaseFragment

class TimerFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_timer

    private val mViewModel: TimerViewModel by lazy { getViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
        subscribeToViewModel()
    }

    private fun initButtons() {
        nextButton.setOnClickListener { mViewModel.onNextDate() }
        previousButton.setOnClickListener { mViewModel.onPreviousDate() }
        dateTitleClickableArea.setOnClickListener { mViewModel.onCurrentDatePicked() }
        dateTitleClickableArea.setOnLongClickListener { mViewModel.onOpenDatePicker() }
    }

    private fun subscribeToViewModel() {
        mViewModel.dateViewState.observe(viewLifecycleOwner, Observer { viewState ->
            dateTitle.text = viewState
        })
    }

    companion object {
        fun newInstance() = TimerFragment()
    }

}
