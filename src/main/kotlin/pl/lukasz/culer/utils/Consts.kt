package pl.lukasz.culer.utils

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

class Consts {
    companion object {
        val T_RULE_MEMBERSHIP = F(1.0)         //default and constant terminal rule membership
        val N_RULE_MEMBERSHIP = F(0.0)        //just initial non-terminal rule membership
        val FULL_MEMBERSHIP = F(1.0)
        val DO_NOT_BELONG_AT_ALL = F(0.0)
        val FULL_RELEVANCE = F(1.0)
        val NOT_RELEVANT_AT_ALL = F(0.0)

        val DEFAULT_THRESHOLD = 0.5

        const val DEFAULT_START_SYMBOL = '$'
        val END_STRING_SYMBOL = NSymbol('Γ')

        const val N_GEN_START = 'A'
        const val MEMBERSHIP_SHORT_FORMATTER = "%.2f"
    }
}