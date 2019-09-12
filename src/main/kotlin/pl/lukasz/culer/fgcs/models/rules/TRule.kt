package pl.lukasz.culer.fgcs.models.rules

import pl.lukasz.culer.fgcs.models.symbols.Symbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.T_RULE_MEMBERSHIP

class TRule(
    left : Symbol,
    right : Symbol) : Rule(left, arrayOf(right), IntervalFuzzyNumber(T_RULE_MEMBERSHIP)) {
    //region public
    fun getRight() = right[0]
    //endregion
}