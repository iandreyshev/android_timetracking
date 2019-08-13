package ru.iandreyshev.timemanager.ui.menu

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.ui.BaseFragment

class MenuFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.fragment_menu

    private val mViewModel: MenuViewModel by lazy {
        ViewModelProviders.of(this)[MenuViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = MenuFragment()
    }

}