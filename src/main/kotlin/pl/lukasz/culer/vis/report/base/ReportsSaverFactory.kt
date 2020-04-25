package pl.lukasz.culer.vis.report.base

import pl.lukasz.culer.fuzzy.processors.heatmap.EqualTreesHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.MaxMembershipHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.RelavanceWeightedHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.vis.report.TextReportsSaver

enum class ReportsSaverFactory : () -> ReportsSaver {
    TEXT_REPORT {
        override fun invoke(): ReportsSaver = TextReportsSaver()
    }
}