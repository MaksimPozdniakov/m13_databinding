package project.gb.searchbar.ui.main

sealed class State {
    data object Loading : State()
    data object Success : State()
}