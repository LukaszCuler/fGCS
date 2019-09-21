package pl.lukasz.culer.fuzzy

import pl.lukasz.culer.settings.Settings

class IntervalFuzzyNumber(var lowerBound : Double = 0.0,
                          var upperBound : Double  = 0.0) : Comparable<IntervalFuzzyNumber> {
    override fun compareTo(other: IntervalFuzzyNumber) = midpoint.compareTo(other.midpoint)
    //region properties
    val midpoint get() = (lowerBound+upperBound)/2.0
    //endregion
    //region constructors
    constructor(exactValue : Double) : this() {
        lowerBound = exactValue
        upperBound = exactValue
    }
    //endregion
    //region public
    override fun toString() = "[$lowerBound,$upperBound]"
    //endregion
}