package pl.lukasz.culer.fuzzy.processors.heatmap.base

import pl.lukasz.culer.fuzzy.processors.heatmap.MinMaxHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor

enum class HeatmapProcessorFactory : () -> HeatmapProcessor {
    MINMAX {
        override fun invoke(): HeatmapProcessor = MinMaxHeatmapProcessor()
    }
}