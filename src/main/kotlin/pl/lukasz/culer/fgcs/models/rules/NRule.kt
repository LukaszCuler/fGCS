package pl.lukasz.culer.fgcs.models.rules

import pl.lukasz.culer.fgcs.models.symbols.Symbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts.Companion.N_RULE_MEMBERSHIP

class NRule(
    left : Symbol,
    right : Array<Symbol>,
    membership : IntervalFuzzyNumber = IntervalFuzzyNumber(N_RULE_MEMBERSHIP)) : Rule(left, right, membership) {
    //region public
    fun getRightFirst() = right[0]
    fun getRightSecond() = right[1]
    //endregion
}