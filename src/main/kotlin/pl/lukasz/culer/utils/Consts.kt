package pl.lukasz.culer.utils

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

class Consts {
    companion object {
        val T_RULE_MEMBERSHIP = IntervalFuzzyNumber(1.0)         //default and constant terminal rule membership
        val N_RULE_MEMBERSHIP = IntervalFuzzyNumber(0.0)        //just initial non-terminal rule membership
        val DO_NOT_BELONG_AT_ALL = IntervalFuzzyNumber(0.0)
        val FULL_RELEVANCE = IntervalFuzzyNumber(1.0)
        val NOT_RELEVANT_AT_ALL = IntervalFuzzyNumber(0.0)

        const val DEFAULT_START_SYMBOL = '$'
        val END_STRING_SYMBOL = NSymbol('Γ')

        const val N_GEN_START = 'A'
    }
}