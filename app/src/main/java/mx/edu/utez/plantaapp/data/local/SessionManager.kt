package mx.edu.utez.plantaapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mx.edu.utez.plantaapp.data.remote.dto.UsuarioDto

private val Context.dataStore by preferencesDataStore(name = "plantaapp_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_FULL_NAME = stringPreferencesKey("full_name")
        private val KEY_CORREO = stringPreferencesKey("correo")
    }

    val userFlow: Flow<UsuarioDto?> = context.dataStore.data.map { prefs ->
        val id = prefs[KEY_USER_ID]
        val name = prefs[KEY_FULL_NAME]
        val correo = prefs[KEY_CORREO]
        if (id != null && name != null && correo != null) {
            UsuarioDto(id = id, fullName = name, correo = correo)
        } else {
            null
        }
    }

    suspend fun saveSession(usuario: UsuarioDto) {
        val id = usuario.id ?: return
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = id
            prefs[KEY_FULL_NAME] = usuario.fullName
            prefs[KEY_CORREO] = usuario.correo
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}