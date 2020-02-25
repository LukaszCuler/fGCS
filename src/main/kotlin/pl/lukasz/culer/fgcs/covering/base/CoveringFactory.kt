package pl.lukasz.culer.fgcs.covering.base

import pl.lukasz.culer.fgcs.covering.CompletingCovering
import pl.lukasz.culer.fgcs.measures.grammar.EntropyGrammarPerfectionMeasure
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarPerfectionMeasure

enum class CoveringFactory : () -> Covering {
    COMPLETING {
        override fun invoke(): Covering = CompletingCovering()
    }
}