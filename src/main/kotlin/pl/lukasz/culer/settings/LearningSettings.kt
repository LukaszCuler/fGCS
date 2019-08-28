package pl.lukasz.culer.settings

import pl.lukasz.culer.fuzzy.snorms.SNormT2
import pl.lukasz.culer.fuzzy.tnorms.TNormT2

class LearningSettings {
    companion object {
        val instance = LearningSettings()
    }

    val sNorm = SNormT2.MAX
    val tNorm = TNormT2.MIN
}