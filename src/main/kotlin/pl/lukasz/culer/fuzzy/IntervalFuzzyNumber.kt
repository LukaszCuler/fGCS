package pl.lukasz.culer.fuzzy

import pl.lukasz.culer.settings.Settings

class IntervalFuzzyNumber(var lowerBound : Double = 0.0,
                          var upperBound : Double  = 0.0) {
    companion object {
        lateinit var settings: Settings     //shouldn't be like that
    }
    //region constructors
    constructor(exactValue : Double) : this() {
        lowerBound = exactValue
        upperBound = exactValue
    }
    //endregion
    //region operators
    operator fun plus(second: IntervalFuzzyNumber): IntervalFuzzyNumber {
        return settings.sNorm(this, second)
    }

    operator fun times(second: IntervalFuzzyNumber): IntervalFuzzyNumber {
        return settings.tNorm(this, second)
    }
    //endregion
    //region public
    override fun toString() = "[$lowerBound,$upperBound]"
    //endregion
}