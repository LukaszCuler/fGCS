package pl.lukasz.culer.vis.report

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.reports.FinalResult
import pl.lukasz.culer.fgcs.models.reports.InitData
import pl.lukasz.culer.fgcs.models.reports.Iteration
import pl.lukasz.culer.utils.Consts.Companion.GENERATED_REPORTS_PATH
import pl.lukasz.culer.utils.TextReport
import pl.lukasz.culer.vis.report.base.ReportsSaver

class TextReportsSaver : ReportsSaver, TextReport() {
    //region consts
    companion object {
        const val REPORT_EXTENSION = ".txt"
        const val START_REPORT = "tr_start.txt"
        const val INFERENCE_START = "tr_inference_start.txt"
    }
    //endregion
    override fun initialize(reportName: String) {
        initReport(GENERATED_REPORTS_PATH+reportName+REPORT_EXTENSION,
            getTemplate(START_REPORT)
                .format(reportName))
    }

    override fun saveInferenceInitialData(initialData: InitData) {
        addToReport(getTemplate(INFERENCE_START))
    }

    override fun saveIteration(iteration: Iteration) {
        TODO("Not yet implemented")
    }

    override fun saveInferenceFinalData(finalResult: FinalResult) {
        TODO("Not yet implemented")
    }

    override fun finalize() {
        TODO("Not yet implemented")
    }

    override fun saveTestResults(testExamples: List<FGCS.ExampleAnalysisResult>) {
        TODO("Not yet implemented")
    }
    //private methods
}