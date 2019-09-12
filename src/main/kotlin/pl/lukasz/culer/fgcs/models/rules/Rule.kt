package pl.lukasz.culer.fgcs.models.rules

import pl.lukasz.culer.fgcs.models.symbols.Symbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

abstract class Rule (
    val left : Symbol,
    val right : Array<Symbol>,
    var membership : IntervalFuzzyNumber
    ){
    //temporary properties
    var vitalLength = IntervalFuzzyNumber(0.0)
    var vitalDirection = IntervalFuzzyNumber(0.0)

    var sideLength = IntervalFuzzyNumber(0.0)
    var sideDirection = IntervalFuzzyNumber(0.0)
}