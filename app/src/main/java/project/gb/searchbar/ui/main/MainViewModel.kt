package project.gb.searchbar.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel : ViewModel() {

    private val repository = MainRepository()

    val state = MutableStateFlow<State>(State.Success)

    private val requestString = MutableStateFlow("")
    val responseString = MutableStateFlow("")
    private var searchProcess: Job? = null

    private suspend fun searchString() {
        state.value = State.Loading
        responseString.value = repository.getData(requestString.value)
        state.value = State.Success
    }

    @OptIn(FlowPreview::class)
    fun updateSearchText(str: String) {
        searchProcess?.cancel()

        if (str.length < 3) {
            state.value = State.Error("Запрос должен быть не менее 3-х символов")
        } else {
            requestString.value = str
            searchProcess = requestString
                .debounce(1000)
                .onEach {
                    searchString()
                }
                .launchIn(viewModelScope)
        }
    }
}