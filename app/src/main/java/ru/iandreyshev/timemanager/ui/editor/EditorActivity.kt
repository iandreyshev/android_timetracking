package ru.iandreyshev.timemanager.ui.editor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_editor.*
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.domain.cards.CardId
import ru.iandreyshev.timemanager.domain.cards.EventId
import ru.iandreyshev.timemanager.ui.BaseActivity
import ru.iandreyshev.timemanager.utils.exhaustive

class EditorActivity : BaseActivity() {

    private var mDatePickerDialog: DatePickerDialog? = null
    private var mTimePickerDialog: TimePickerDialog? = null

    private val mViewModel: EditorViewModel by lazy {
        val cardId = CardId(intent.extras?.getLong(ARG_CARD_ID) ?: 0)
        val eventId = intent.extras?.getLong(ARG_EVENT_ID)?.let { EventId(it) }
        getViewModel(cardId, eventId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        startDateGroup.setOnClickListener { mViewModel.onStartDatePickerClick() }
        startTimeGroup.setOnClickListener { mViewModel.onStartTimePickerClick() }
        endDateGroup.setOnClickListener { mViewModel.onEndDatePickerClick() }
        endTimeGroup.setOnClickListener { mViewModel.onEndTimePickerClick() }

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.editor_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)

        mViewModel.datePicker.observe(::updateDatePicker)

        mViewModel.startDatePreview.observe(::updateStartDate)
        mViewModel.startTimePreview.observe(::updateStartTime)
        mViewModel.startDateTimeComment.observe {
            updateDateTimeComment(startDateTimeComment, it)
        }

        mViewModel.endDatePreview.observe(::updateEndDate)
        mViewModel.endTimePreview.observe(endTime::setText)
        mViewModel.endDateTimeComment.observe {
            updateDateTimeComment(endDateTimeComment, it)
        }

        mViewModel.updateTitleEvent.consume(titleView::setText)
        mViewModel.exitEvent.consume { finish() }
        mViewModel.showErrorEvent.consume { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        titleView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) =
                mViewModel.onTitleChanged(p0)
        })

        if (savedInstanceState == null) {
            titleView.requestFocus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.submit -> mViewModel.onSaveClicked()
        }
        return true
    }

    override fun onBackPressed() {
        mViewModel.onBackPressed()
    }

    private fun updateDatePicker(viewState: DatePickerViewState) {
        when (viewState) {
            is DatePickerViewState.StartDate ->
                updateStartDatePicker(viewState)
            is DatePickerViewState.StartTime ->
                updateStartTimePicker(viewState)
            is DatePickerViewState.EndDate ->
                updateEndDatePicker(viewState)
            is DatePickerViewState.EndTime ->
                updateEndTimePicker(viewState)
            is DatePickerViewState.Hidden ->
                dismissDialogs()
        }.exhaustive
    }

    private fun updateStartDatePicker(state: DatePickerViewState.StartDate) {
        dismissDialogs()
        mDatePickerDialog = DatePickerDialog(
            this,
            state.listener,
            state.date.year,
            state.date.month.value,
            state.date.dayOfMonth
        ).apply {
            setTitle(R.string.editor_start_date_title)
            show()
        }
    }

    private fun updateStartTimePicker(state: DatePickerViewState.StartTime) {
        dismissDialogs()
        mTimePickerDialog = TimePickerDialog(
            this,
            state.listener,
            state.time.hour,
            state.time.minute,
            true
        ).apply {
            setTitle(R.string.editor_start_time_title)
            show()
        }
    }

    private fun updateEndDatePicker(state: DatePickerViewState.EndDate) {
        dismissDialogs()
        mDatePickerDialog = DatePickerDialog(
            this,
            state.listener,
            state.date.year,
            state.date.month.value,
            state.date.dayOfMonth
        ).apply {
            setTitle(R.string.editor_end_date_title)
            show()
        }
    }

    private fun updateEndTimePicker(state: DatePickerViewState.EndTime) {
        dismissDialogs()
        mTimePickerDialog = TimePickerDialog(
            this,
            state.listener,
            state.time.hour,
            state.time.minute,
            true
        ).apply {
            setTitle(R.string.editor_end_time_title)
            show()
        }
    }

    private fun dismissDialogs() {
        mDatePickerDialog?.dismiss()
        mDatePickerDialog = null
        mTimePickerDialog?.dismiss()
        mTimePickerDialog = null
    }

    private fun updateStartDate(viewState: StartDateViewState) {
        when (viewState) {
            is StartDateViewState.Today -> {
                startDateGroup.isGone = false
                startDate.text = getString(R.string.editor_start_date_today, viewState.value)
            }
            is StartDateViewState.ShowDate -> {
                startDateGroup.isGone = false
                startDate.text = viewState.value
            }
        }.exhaustive
    }

    private fun updateStartTime(viewState: StartTimeViewState) {
        when (viewState) {
            StartTimeViewState.Undefined -> {
                startTimeGroup.isGone = false
                startTime.text = getString(R.string.editor_start_time_undefined)
            }
            is StartTimeViewState.ShowTime -> {
                startTimeGroup.isGone = false
                startTime.text = viewState.value
            }
        }.exhaustive
    }

    private fun updateEndDate(viewState: EndDateViewState) {
        when (viewState) {
            is EndDateViewState.Today -> {
                endDateGroup.isGone = false
                endDate.text = getString(R.string.editor_start_date_today, viewState.value)
            }
            is EndDateViewState.ShowDate -> {
                endDateGroup.isGone = false
                endDate.text = viewState.value
            }
        }.exhaustive
    }

    private fun updateDateTimeComment(
        commentView: TextView,
        comment: DatePickerCommentViewState
    ) {
        commentView.isVisible = true
        commentView.text = when (comment) {
            DatePickerCommentViewState.Hidden -> {
                commentView.isVisible = false
                commentView.text
            }
            DatePickerCommentViewState.JustNow ->
                getString(R.string.editor_start_description_just_now)
            is DatePickerCommentViewState.RightAfter ->
                getString(R.string.editor_start_description_right_after, comment.event)
            is DatePickerCommentViewState.ErrorStartBeforePrevious ->
                getString(
                    R.string.editor_datetime_description_error_start_before_previous,
                    comment.event
                )
            DatePickerCommentViewState.ErrorEndBeforeStart ->
                getString(R.string.editor_datetime_description_error_end_before_start)
        }.exhaustive
    }

    companion object {
        const val ARG_CARD_ID = "arg:card_id"
        const val ARG_EVENT_ID = "arg:event"
    }

}
