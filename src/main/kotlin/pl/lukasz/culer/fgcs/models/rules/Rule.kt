package pl.lukasz.culer.fgcs.models.rules

import com.google.gson.annotations.SerializedName
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.Symbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

abstract class Rule<T : Symbol> (
    @SerializedName("left")
    val left : NSymbol,
    @SerializedName("right")
    val right : Array<T>,
    @SerializedName("membership")
    var membership : IntervalFuzzyNumber
    ){
    //temporary properties
    @Transient
    var vitalLength = IntervalFuzzyNumber(0.0)
    @Transient
    var vitalDirection = IntervalFuzzyNumber(0.0)

    @Transient
    var sideLength = IntervalFuzzyNumber(0.0)
    @Transient
    var sideDirection = IntervalFuzzyNumber(0.0)


    //boilerplate stuff
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule<*>

        if (left != other.left) return false
        if (!right.contentEquals(other.right)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.contentHashCode()
        return result
    }


}