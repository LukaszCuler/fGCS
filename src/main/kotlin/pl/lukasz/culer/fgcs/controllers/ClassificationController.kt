package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings

class ClassificationController(val gc: GrammarController,
                               val settings: Settings) {

    fun getFuzzyClassification(example: TestExample) : IntervalFuzzyNumber {
        return IntervalFuzzyNumber()
    }

    fun getCrispClassification(example: TestExample) : Boolean {
        return true
    }

    fun getExampleHeatmap(example: TestExample) : List<IntervalFuzzyNumber> {
        return emptyList()
    }
}