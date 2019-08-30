package pl.lukasz.culer.settings

import com.google.gson.Gson
import pl.lukasz.culer.fuzzy.snorms.SNormT2
import pl.lukasz.culer.fuzzy.tnorms.TNormT2
import pl.lukasz.culer.utils.Logger
import pl.lukasz.culer.utils.SETTINGS_LOADING
import java.io.File

const val TAG = "Settings"

class Settings {

    companion object {
        fun loadFromObject(filename : String?) : Settings {
            Logger.instance.d(TAG, SETTINGS_LOADING)
            return Gson().fromJson<Settings>(File(filename).readText(), Settings::class.java)
        }
    }

    val sNorm = SNormT2.MAX
    val tNorm = TNormT2.MIN
}