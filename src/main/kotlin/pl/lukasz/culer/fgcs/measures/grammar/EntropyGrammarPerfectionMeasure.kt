package pl.lukasz.culer.fgcs.measures.grammar

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarPerfectionMeasure
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts

/**
 * based on - Kosko, Bart. "Fuzzy entropy and conditioning." Information sciences 40.2 (1986): 165-174.
 * should not be applied "alone" as measure for grammar fitness.
 * Entropy of -RULES- not examples are counted
 * 1 - entirely fuzzified, 0 - not fuzzified at all
 */
class EntropyGrammarPerfectionMeasure : GrammarPerfectionMeasure() {
    companion object {
        const val NOT_FUZZIFIED = 0.0
        const val FULLY_FUZZIFIED = 1.0
        const val MIDDLE = 0.5
    }

    override fun getDoubleMeasure(grammar: Grammar, examples :List<FGCS.ExampleAnalysisResult>): Double {
        if(grammar.nRules.isEmpty()) return NOT_FUZZIFIED

        var nearSum = F()
        var farSum = F()

        for(rule in grammar.nRules){
            if(rule.membership.midpoint >= MIDDLE){
                nearSum += IntervalFuzzyNumber.abs(rule.membership - Consts.FULL_MEMBERSHIP)
                farSum += rule.membership
            } else {
                farSum += IntervalFuzzyNumber.abs(rule.membership - Consts.FULL_MEMBERSHIP)
                nearSum += rule.membership
            }
        }

        return nearSum.midpoint / farSum.midpoint
    }

    override fun isGrammarPerfect(measureValue : Double) = measureValue == NOT_FUZZIFIED //introduce eps?
}