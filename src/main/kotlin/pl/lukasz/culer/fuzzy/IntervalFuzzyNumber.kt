package pl.lukasz.culer.fuzzy

import pl.lukasz.culer.settings.LearningSettings

class IntervalFuzzyNumber(var lowerBound : Double = 0.0,
                          var upperBound : Double  = 0.0) {
    //region constructors
    constructor(exactValue : Double) : this() {
        lowerBound = exactValue
        upperBound = exactValue
    }
    //endregion
    //region operators
    operator fun plus(second: IntervalFuzzyNumber): IntervalFuzzyNumber {
        return LearningSettings.instance.sNorm(this, second)
    }

    operator fun times(second: IntervalFuzzyNumber): IntervalFuzzyNumber {
        return LearningSettings.instance.tNorm(this, second)
    }
    //endregion
    //region public
    override fun toString() = "[$lowerBound,$upperBound]"
    //endregion
}