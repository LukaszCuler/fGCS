package pl.lukasz.culer.fuzzy.processors.heatmap.base

import pl.lukasz.culer.fuzzy.processors.heatmap.MaxMembershipHeatmapProcessor

//@TODO unit tests!
enum class HeatmapProcessorFactory : () -> HeatmapProcessor {
    MINMAX {
        override fun invoke(): HeatmapProcessor = MaxMembershipHeatmapProcessor()
    }
}