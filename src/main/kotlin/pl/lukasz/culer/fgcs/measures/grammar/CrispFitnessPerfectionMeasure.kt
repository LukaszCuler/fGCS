package pl.lukasz.culer.fgcs.measures.grammar

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarPerfectionMeasure
import pl.lukasz.culer.fgcs.models.Grammar

class CrispFitnessPerfectionMeasure : GrammarPerfectionMeasure() {
    companion object {
        const val FULL_FITNESS = 1.0
    }
    override fun getDoubleMeasure(grammar : Grammar, examples: List<FGCS.ExampleAnalysisResult>): Double {
        if(examples.isEmpty()) return FULL_FITNESS
        val positives = examples.map { it.crispClassification }.filter { it }.size
        return positives.toDouble() / examples.size.toDouble()
    }

    override fun isGrammarPerfect(measureValue : Double) = measureValue == FULL_FITNESS
}