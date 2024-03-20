package project.gb.searchbar.ui.main

import kotlinx.coroutines.delay

class MainRepository {
    suspend fun getData(str: String) : String {
        delay(2000)
        return "По вашему запросу '$str' не удалось ничего найти!"
    }
}