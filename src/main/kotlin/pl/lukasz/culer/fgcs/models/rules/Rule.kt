package pl.lukasz.culer.fgcs.models.rules

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.Symbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

abstract class Rule (
    val left : NSymbol,
    val right : Array<out Symbol>,
    var membership : IntervalFuzzyNumber
    ){
    //temporary properties
    var vitalLength = IntervalFuzzyNumber(0.0)
    var vitalDirection = IntervalFuzzyNumber(0.0)

    var sideLength = IntervalFuzzyNumber(0.0)
    var sideDirection = IntervalFuzzyNumber(0.0)


    //boilerplate stuff
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule

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