package ru.iandreyshev.timemanager.ui.tutorial

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_tutorial.*
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.di.getViewModel
import ru.iandreyshev.timemanager.ui.BaseActivity

class TutorialActivity : BaseActivity() {

    private val mViewModel: TutorialViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        cancelButton.setOnClickListener {
            mViewModel.onCancelTutorial()
        }
    }

}
