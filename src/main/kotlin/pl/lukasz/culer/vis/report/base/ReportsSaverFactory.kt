package pl.lukasz.culer.vis.report.base

import pl.lukasz.culer.vis.report.TextReportsSaver

enum class ReportsSaverFactory : () -> ReportsSaver {
    TEXT_REPORT {
        override fun invoke(): ReportsSaver = TextReportsSaver()
    }
}