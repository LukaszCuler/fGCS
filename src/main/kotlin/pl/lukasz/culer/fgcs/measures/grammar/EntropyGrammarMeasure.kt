package pl.lukasz.culer.fgcs.measures.grammar

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarMeasure
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import kotlin.math.abs

/**
 * @TODO UT
 * based on - Kosko, Bart. "Fuzzy entropy and conditioning." Information sciences 40.2 (1986): 165-174.
 * should not be applied "alone" as measure for grammar fitness.
 * Entropy of -RULES- not examples are counted
 * 1 - entirely fuzzified, 0 - not fuzzified at all
 */
class EntropyGrammarMeasure : GrammarMeasure() {
    companion object {
        const val NOT_FUZZIFIED = 0.0
        const val MIDDLE = 0.5
    }

    override fun getDoubleMeasure(grammar: Grammar, examples :List<FGCS.ExampleAnalysisResult>): Double {
        if(grammar.nRules.isEmpty()) return NOT_FUZZIFIED

        var nearSum = F(0.0)
        var farSum = F(0.0)

        for(rule in grammar.nRules){
            if(rule.membership.midpoint >= MIDDLE){
                nearSum += IntervalFuzzyNumber.abs(rule.membership - 1.0)
                farSum += rule.membership
            } else {
                farSum += IntervalFuzzyNumber.abs(rule.membership - 1.0)
                nearSum += rule.membership
            }
        }

        return nearSum.midpoint / farSum.midpoint
    }

    override fun isGrammarPerfect(grammar: Grammar, examples :List<FGCS.ExampleAnalysisResult>): Boolean {
        return getDoubleMeasure(grammar, examples) == 0.0 //introduce eps?
    }
}