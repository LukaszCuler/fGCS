package pl.lukasz.culer.fuzzy

import com.google.gson.annotations.SerializedName
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.MEMBERSHIP_SHORT_FORMATTER

typealias F = IntervalFuzzyNumber
//@TODO UT?
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
    //region static methods, to mimic original method
    companion object {
        fun abs(i: IntervalFuzzyNumber): IntervalFuzzyNumber {
            if(i.lowerBound <= 0 && i.upperBound >= 0) {
                var upper =
                    if(kotlin.math.abs(i.lowerBound) > i.upperBound) kotlin.math.abs(i.lowerBound)
                    else i.upperBound
                return IntervalFuzzyNumber(0.0, upper)
            }

            val a = kotlin.math.abs(i.lowerBound)
            val b = kotlin.math.abs(i.upperBound)

            if(a>b) return IntervalFuzzyNumber(b,a)
            return IntervalFuzzyNumber(a,b)
        }
    }
    //region
    //region public
    override fun toString() = "[${MEMBERSHIP_SHORT_FORMATTER.format(lowerBound)};${MEMBERSHIP_SHORT_FORMATTER.format(upperBound)}]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass == other?.javaClass){
            other as IntervalFuzzyNumber

            if (lowerBound != other.lowerBound) return false
            if (upperBound != other.upperBound) return false

            return true
        } else if(lowerBound == upperBound){
            return lowerBound == upperBound && upperBound == other
        }

        return false
    }

    override fun hashCode(): Int {
        var result = lowerBound.hashCode()
        result = 31 * result + upperBound.hashCode()
        return result
    }
    //endregion
    //region operators overloading section
    //with each other
    operator fun plus(second: IntervalFuzzyNumber): IntervalFuzzyNumber {
        return IntervalFuzzyNumber(lowerBound + second.lowerBound, upperBound + second.upperBound)
    }

    operator fun minus(second: IntervalFuzzyNumber): IntervalFuzzyNumber {
        return IntervalFuzzyNumber(lowerBound - second.lowerBound, upperBound - second.upperBound)
    }

    operator fun times(second: IntervalFuzzyNumber): IntervalFuzzyNumber {
        return IntervalFuzzyNumber(lowerBound * second.lowerBound, upperBound * second.upperBound)
    }

    //@TODO should be solved differently
    operator fun div(second: IntervalFuzzyNumber): IntervalFuzzyNumber {
        return IntervalFuzzyNumber(if(second.lowerBound != 0.0) lowerBound / second.lowerBound else 0.0,
            if(second.upperBound != 0.0) upperBound / second.upperBound else 0.0)
    }

    //with number
    operator fun plus(second: Double): IntervalFuzzyNumber {
        return IntervalFuzzyNumber(lowerBound + second, upperBound + second)
    }

    operator fun minus(second: Double): IntervalFuzzyNumber {
        return IntervalFuzzyNumber(lowerBound - second, upperBound - second)
    }

    operator fun times(second: Double): IntervalFuzzyNumber {
        return IntervalFuzzyNumber(lowerBound * second, upperBound * second)
    }

    //@TODO should be solved differently
    operator fun div(second: Double): IntervalFuzzyNumber {
        return IntervalFuzzyNumber(if(second != 0.0) lowerBound / second else 0.0,
            if(second != 0.0) upperBound / second else 0.0)
    }

    //endregion
}