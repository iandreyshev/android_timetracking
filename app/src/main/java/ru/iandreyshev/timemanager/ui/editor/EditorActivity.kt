package ru.iandreyshev.timemanager.ui.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_editor.*
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel

class EditorActivity : AppCompatActivity() {

    private val mViewModel: EditorViewModel by lazy { getViewModel(ZonedDateTime.now(), null) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_editor)

        dateGroup.setOnClickListener { }
        timeGroup.setOnClickListener { }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.submit -> mViewModel.onSave(collectEditorData())
        }
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    private fun collectEditorData(): EditorEvent = TODO()

}
