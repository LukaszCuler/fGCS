package pl.lukasz.culer.fgcs.models.rules

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.Symbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.T_RULE_MEMBERSHIP

class TRule(
    left : NSymbol,
    right : TSymbol) : Rule<TSymbol>(left, arrayOf(right), T_RULE_MEMBERSHIP) {
    //region public
    fun getRight() = right[0]
    //endregion
}