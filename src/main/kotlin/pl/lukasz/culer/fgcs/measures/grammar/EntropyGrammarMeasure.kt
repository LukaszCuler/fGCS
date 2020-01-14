package pl.lukasz.culer.fgcs.measures.grammar

import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarMeasure
import pl.lukasz.culer.fgcs.models.Grammar

//should not be applied "alone" as measure for grammar fitness
class EntropyGrammarMeasure : GrammarMeasure() {
    override fun getDoubleMeasure(grammar: Grammar): Double {
        return 0.0
    }

    override fun isGrammarPerfect(grammar: Grammar): Boolean {
        return true
    }
}