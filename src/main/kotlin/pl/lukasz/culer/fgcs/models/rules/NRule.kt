package pl.lukasz.culer.fgcs.models.rules

import pl.lukasz.culer.annotations.Exclude
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.Symbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts.Companion.N_RULE_MEMBERSHIP
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

typealias NRuleRHS = Pair<NSymbol, NSymbol>

class NRule(
    left : NSymbol,
    right : NRuleRHS,
    membership : IntervalFuzzyNumber = N_RULE_MEMBERSHIP) : Rule<NSymbol>(left, arrayOf(right.first, right.second), membership) {
    //region public
    fun getRightFirst() = right[0]
    fun getRightSecond() = right[1]
    fun updateRightFirst(symbol : NSymbol) {
        right[0] = symbol
    }
    fun updateRightSecond(symbol : NSymbol) {
        right[1] = symbol
    }
    //endregion
    //region overrides
    override fun copy() = NRule(left, NRuleRHS(getRightFirst(), getRightSecond()), membership)
    //endregion
    //region simulation temp parameter
    @Exclude
    var occurredInParsing = AtomicBoolean(false)
    @Exclude
    var analysisTemps = ConcurrentHashMap<Any, Any>() //hate this ;_; but don't want to add maps / params for every methods
    //endregion
}