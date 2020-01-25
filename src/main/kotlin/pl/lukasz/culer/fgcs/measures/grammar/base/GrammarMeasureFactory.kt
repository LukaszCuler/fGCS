package pl.lukasz.culer.fgcs.measures.grammar.base

import pl.lukasz.culer.fgcs.measures.grammar.CrispFitnessPerfectionMeasure
import pl.lukasz.culer.fgcs.measures.grammar.EntropyGrammarPerfectionMeasure

enum class GrammarMeasureFactory : () -> GrammarPerfectionMeasure {
    ENTROPY {
        override fun invoke(): GrammarPerfectionMeasure = EntropyGrammarPerfectionMeasure()
    },
    CRISP_FITNESS {
        override fun invoke(): GrammarPerfectionMeasure = CrispFitnessPerfectionMeasure()
    }
}