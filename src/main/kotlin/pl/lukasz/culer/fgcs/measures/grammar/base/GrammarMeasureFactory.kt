package pl.lukasz.culer.fgcs.measures.grammar.base

import pl.lukasz.culer.fgcs.measures.grammar.EntropyGrammarMeasure
import pl.lukasz.culer.fuzzy.processors.heatmap.EqualTreesHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.MaxMembershipHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.RelavanceWeightedHeatmapProcessor

enum class GrammarMeasureFactory : () -> GrammarMeasure {
    ENTROPY {
        override fun invoke(): GrammarMeasure = EntropyGrammarMeasure()
    }
}