package ru.iandreyshev.timemanager.ui.editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.fragment_editor.*
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import ru.iandreyshev.timemanager.domain.CardId
import ru.iandreyshev.timemanager.ui.BaseActivity

class EditorActivity : BaseActivity() {

    private val mViewModel: EditorViewModel by lazy {
        val cardId = CardId(intent.extras?.getLong(CARD_ID_KEY) ?: 0)
        getViewModel(cardId, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_editor)

        endTimeGroup.setOnClickListener { pickEndTime() }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)

        mViewModel.timeViewState.observe(timeTitle::setText)

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

    private fun pickEndTime() {
        SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .displayMinutes(true)
                .displayHours(true)
                .displayDays(false)
                .displayMonth(false)
                .displayYears(false)
                .minutesStep(1)
                .listener(mViewModel::onTimePicked)
                .display()
    }

    companion object {
        const val CARD_ID_KEY = "card_id"
    }

}
