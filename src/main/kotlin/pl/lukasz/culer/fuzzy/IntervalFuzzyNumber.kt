package pl.lukasz.culer.fuzzy

import com.google.gson.annotations.SerializedName
import pl.lukasz.culer.settings.Settings

class IntervalFuzzyNumber(
    @SerializedName("lowerBound")
    var lowerBound : Double = 0.0,
    @SerializedName("upperBound")
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