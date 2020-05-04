package pl.lukasz.culer.fgcs.models.reports

import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.settings.Settings

data class InitData(val inputSet : List<TestExample>,
                    val testSet : List<TestExample>? = null,
                    val maxIterations : Int?,
                    val settings : Settings)