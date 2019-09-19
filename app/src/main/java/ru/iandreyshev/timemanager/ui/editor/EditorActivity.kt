package ru.iandreyshev.timemanager.ui.editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isGone
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import kotlinx.android.synthetic.main.fragment_editor.*
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.domain.CardId
import ru.iandreyshev.timemanager.domain.EventId
import ru.iandreyshev.timemanager.ui.BaseActivity
import ru.iandreyshev.timemanager.utils.exhaustive

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

        startTimeGroup.setOnClickListener { pickStartTime() }
        endTimeGroup.setOnClickListener { pickEndTime() }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)

        mViewModel.startTimeViewState.observe(::updateStartTime)
        mViewModel.endTimeViewState.observe(endTime::setText)
        mViewModel.updateTitleEvent.consume(titleView::setText)
        mViewModel.exitEvent.consume { finish() }

        titleView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = mViewModel.onTitleChanged(p0)
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
            R.id.submit -> mViewModel.onSave()
        }
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    private fun pickStartTime() {
        mTimePickerDialog?.dismiss()
        mTimePickerDialog = SingleDateAndTimePickerDialog.Builder(this)
            .bottomSheet()
            .displayMinutes(true)
            .displayHours(true)
            .displayDays(false)
            .displayMonth(false)
            .displayYears(false)
            .minutesStep(1)
            .listener(mViewModel::onStartTimePicked)
            .build()
        mTimePickerDialog?.display()
    }

    private fun pickEndTime() {
        mTimePickerDialog?.dismiss()
        mTimePickerDialog = SingleDateAndTimePickerDialog.Builder(this)
            .bottomSheet()
            .displayMinutes(true)
            .displayHours(true)
            .displayDays(false)
            .displayMonth(false)
            .displayYears(false)
            .minutesStep(1)
            .listener(mViewModel::onEndTimePicked)
            .build()
        mTimePickerDialog?.display()
    }

    private fun updateStartTime(viewState: StartTimeViewState) {
        when (viewState) {
            StartTimeViewState.Hidden ->
                startTimeGroup.isGone = true
            is StartTimeViewState.ShowTime -> {
                startTimeGroup.isGone = false
                startTime.text = viewState.value
            }
        }.exhaustive
    }

    companion object {
        const val ARG_CARD_ID = "arg:card_id"
        const val ARG_EVENT_ID = "arg:event"
    }

}
