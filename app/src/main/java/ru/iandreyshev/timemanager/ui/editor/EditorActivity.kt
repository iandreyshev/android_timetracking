package ru.iandreyshev.timemanager.ui.editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import kotlinx.android.synthetic.main.fragment_editor.*
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.domain.CardId
import ru.iandreyshev.timemanager.domain.EventId
import ru.iandreyshev.timemanager.ui.BaseActivity
import ru.iandreyshev.timemanager.utils.exhaustive
import java.util.*

class EditorActivity : BaseActivity() {

    private var mTimePickerDialog: SingleDateAndTimePickerDialog? = null

    private val mViewModel: EditorViewModel by lazy {
        val cardId = CardId(intent.extras?.getLong(ARG_CARD_ID) ?: 0)
        val eventId = intent.extras?.getLong(ARG_EVENT_ID)?.run { EventId(this) }
        getViewModel(cardId, eventId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_editor)

        startDateGroup.setOnClickListener { mViewModel.onStartDatePickerClick() }
        startTimeGroup.setOnClickListener { mViewModel.onStartTimePickerClick() }
        endDateGroup.setOnClickListener { mViewModel.onEndDatePickerClick() }
        endTimeGroup.setOnClickListener { mViewModel.onEndTimePickerClick() }

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.editor_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)

        mViewModel.datePicker.observe(::updateDatePicker)

        mViewModel.startDateTimeAvailable.observe(::updateStartDateTime)
        mViewModel.startDatePreview.observe(::updateStartDate)
        mViewModel.startTimePreview.observe(::updateStartTime)

        mViewModel.endDatePreview.observe(::updateEndDate)
        mViewModel.endTimePreview.observe(endTime::setText)

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
        finish()
    }

    private fun updateDatePicker(viewState: DatePickerViewState) {
        when (viewState) {
            is DatePickerViewState.StartDate -> updateStartDatePicker(
                viewState.default,
                viewState.listener
            )
            is DatePickerViewState.StartTime -> updateStartTimePicker(
                viewState.default,
                viewState.listener
            )
            is DatePickerViewState.EndDate -> updateEndDatePicker(
                viewState.default,
                viewState.listener
            )
            is DatePickerViewState.EndTime -> updateEndTimePicker(
                viewState.default,
                viewState.listener
            )
        }.exhaustive
    }

    private fun updateStartDatePicker(default: Date, listener: (Date?) -> Unit) =
        displayDateTimeDialog(default, R.string.editor_start_date_title, listener) {
            displayMinutes(false)
                .displayHours(false)
                .displayDays(false)
                .displayMonth(true)
                .displayYears(true)
                .displayDaysOfMonth(true)
        }

    private fun updateStartTimePicker(default: Date, listener: (Date?) -> Unit) =
        displayDateTimeDialog(default, R.string.editor_start_time_title, listener) {
            displayMinutes(true)
                .displayHours(true)
                .displayDays(false)
                .displayMonth(false)
                .displayYears(false)
                .minutesStep(1)
        }

    private fun updateEndDatePicker(default: Date, listener: (Date?) -> Unit) =
        displayDateTimeDialog(default, R.string.editor_end_date_title, listener) {
            displayMinutes(false)
                .displayHours(false)
                .displayDays(false)
                .displayMonth(true)
                .displayYears(true)
                .displayDaysOfMonth(true)
        }

    private fun updateEndTimePicker(default: Date, listener: (Date?) -> Unit) =
        displayDateTimeDialog(default, R.string.editor_end_time_title, listener) {
            displayMinutes(true)
                .displayHours(true)
                .displayDays(false)
                .displayMonth(false)
                .displayYears(false)
                .minutesStep(1)
        }

    private fun updateStartDateTime(canEditStartDateTime: Boolean) {
        startGroup.isVisible = canEditStartDateTime
    }

    private fun updateStartDate(viewState: StartDateViewState) {
        when (viewState) {
            StartDateViewState.Today -> {
                startDateGroup.isGone = false
                startDate.text = getString(R.string.editor_start_date_today)
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
            EndDateViewState.Hidden ->
                endDateGroup.isGone = true
            EndDateViewState.Today -> {
                endDateGroup.isGone = false
                endDate.text = getString(R.string.editor_start_date_today)
            }
            is EndDateViewState.ShowDate -> {
                endDateGroup.isGone = false
                endDate.text = viewState.value
            }
        }.exhaustive
    }

    private fun displayDateTimeDialog(
        default: Date,
        @StringRes titleRes: Int,
        listener: (Date?) -> Unit,
        buildAction: SingleDateAndTimePickerDialog.Builder.() -> Unit
    ) {
        mTimePickerDialog?.dismiss()
        mTimePickerDialog = SingleDateAndTimePickerDialog.Builder(this)
            .title(getString(titleRes))
            .mainColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, theme))
            .titleTextColor(ResourcesCompat.getColor(resources, android.R.color.white, theme))
            .apply(buildAction)
            .listener(listener)
            .defaultDate(default)
            .build()
        mTimePickerDialog?.display()
    }

    companion object {
        const val ARG_CARD_ID = "arg:card_id"
        const val ARG_EVENT_ID = "arg:event"
    }

}
