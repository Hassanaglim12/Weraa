package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TokenViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("warera_companion_prefs", Context.MODE_PRIVATE)

    private val _apiToken = MutableStateFlow("")
    val apiToken: StateFlow<String> = _apiToken.asStateFlow()

    // Player metrics state
    private val _playerName = MutableStateFlow("General_Titus")
    val playerName: StateFlow<String> = _playerName.asStateFlow()

    private val _faction = MutableStateFlow("Vanguard Order")
    val faction: StateFlow<String> = _faction.asStateFlow()

    private val _militaryPower = MutableStateFlow(452100)
    val militaryPower: StateFlow<Int> = _militaryPower.asStateFlow()

    private val _allianceTag = MutableStateFlow("VNGD")
    val allianceTag: StateFlow<String> = _allianceTag.asStateFlow()

    private val _coordinates = MutableStateFlow("X: 342, Y: 891")
    val coordinates: StateFlow<String> = _coordinates.asStateFlow()

    init {
        // Load the saved token on init
        val savedToken = sharedPreferences.getString("api_token", "") ?: ""
        _apiToken.value = savedToken
        
        // Also load saved player metrics if any, or use realistic defaults
        val savedName = sharedPreferences.getString("player_name", "General_Titus") ?: "General_Titus"
        _playerName.value = savedName
        val savedFaction = sharedPreferences.getString("faction", "Vanguard Order") ?: "Vanguard Order"
        _faction.value = savedFaction
        val savedAlliance = sharedPreferences.getString("alliance_tag", "VNGD") ?: "VNGD"
        _allianceTag.value = savedAlliance
        val savedCoords = sharedPreferences.getString("coordinates", "X: 342, Y: 891") ?: "X: 342, Y: 891"
        _coordinates.value = savedCoords
        val savedPower = sharedPreferences.getInt("military_power", 452100)
        _militaryPower.value = savedPower
    }

    fun saveToken(token: String) {
        _apiToken.value = token
        viewModelScope.launch {
            sharedPreferences.edit().putString("api_token", token).apply()
        }
    }

    fun updateProfile(name: String, factionName: String, tag: String, coords: String, power: Int) {
        _playerName.value = name
        _faction.value = factionName
        _allianceTag.value = tag
        _coordinates.value = coords
        _militaryPower.value = power

        viewModelScope.launch {
            sharedPreferences.edit().apply {
                putString("player_name", name)
                putString("faction", factionName)
                putString("alliance_tag", tag)
                putString("coordinates", coords)
                putInt("military_power", power)
                apply()
            }
        }
    }

    fun clearToken() {
        _apiToken.value = ""
        viewModelScope.launch {
            sharedPreferences.edit().remove("api_token").apply()
        }
    }
}
