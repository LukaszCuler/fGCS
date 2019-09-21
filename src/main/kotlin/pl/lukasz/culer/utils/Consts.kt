package pl.lukasz.culer.utils

import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

class Consts {
    companion object {
        val T_RULE_MEMBERSHIP = IntervalFuzzyNumber(1.0)         //default and constant terminal rule membership
        val N_RULE_MEMBERSHIP = IntervalFuzzyNumber(0.0)        //just initial non-terminal rule membership

        const val DEFAULT_START_SYMBOL = '$'

        const val N_GEN_START = 'A'
    }
}