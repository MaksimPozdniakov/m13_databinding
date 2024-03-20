package project.gb.searchbar.ui.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import project.gb.searchbar.R
import project.gb.searchbar.databinding.FragmentMainBinding
import kotlinx.coroutines.*


class MainFragment : Fragment() {

    // region init
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var constraintSetStart: ConstraintSet
    private lateinit var constraintSetEnd: ConstraintSet
    // endregion

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // region Инициализируем ConstraintSets
        constraintLayout = binding.mainConstraintLayout
        constraintSetStart = ConstraintSet()
        constraintSetStart.clone(constraintLayout)
        constraintSetEnd = ConstraintSet()
        constraintSetEnd.clone(requireContext(), R.layout.fragment_main_expanded)
        // endregion

        binding.textInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                expandEditText()
            }
        }

        binding.mainConstraintLayout.setOnClickListener {
            onEmptyAreaClicked()
            emptyAreaClickedForKeyboard()

        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect {
                if (it is State.Loading) {
                    emptyAreaClickedForKeyboard()
                }
            }
        }
    }

    /**
     * Метод для анимации раскрытия поля ввода
     */
    private fun expandEditText() {
        TransitionManager.beginDelayedTransition(constraintLayout, ChangeBounds().apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
        })
        constraintSetEnd.applyTo(constraintLayout)
    }

    /**
     * Метод для анимации сворачивания поля ввода
     */
    private fun collapseEditText() {
        TransitionManager.beginDelayedTransition(constraintLayout, ChangeBounds().apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
        })
        constraintSetStart.applyTo(constraintLayout)
        //binding.textInputEditText.clearFocus() // Сброс фокуса
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /**
     * Обработчик нажатия на пустую область экрана
     */
    private fun onEmptyAreaClicked() {
        collapseEditText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Метод скрытия виртуальной клавиатуры
 */
fun Fragment.emptyAreaClickedForKeyboard() = WindowCompat
    .getInsetsController(requireActivity().window, requireView())
    .hide(WindowInsetsCompat.Type.ime())
