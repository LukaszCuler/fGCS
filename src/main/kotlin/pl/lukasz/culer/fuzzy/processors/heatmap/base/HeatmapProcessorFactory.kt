package pl.lukasz.culer.fuzzy.processors.heatmap.base

import pl.lukasz.culer.fuzzy.processors.heatmap.MaxMembershipHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.RelavanceWeightedHeatmapProcessor

//@TODO unit tests!
enum class HeatmapProcessorFactory : () -> HeatmapProcessor {
    MINMAX {
        override fun invoke(): HeatmapProcessor = MaxMembershipHeatmapProcessor()
    },
    RELEVANCE_WEIGHTED {
        override fun invoke(): HeatmapProcessor = RelavanceWeightedHeatmapProcessor()
    }
}