package pl.lukasz.culer.vis.report

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.reports.FinalResult
import pl.lukasz.culer.fgcs.models.reports.InitData
import pl.lukasz.culer.fgcs.models.reports.Iteration
import pl.lukasz.culer.utils.TextReport
import pl.lukasz.culer.vis.report.base.ReportsSaver

class TextReportsSaver : ReportsSaver, TextReport() {
    //region consts
    companion object {

    }
    //endregion
    override fun initialize(reportName: String) {
        TODO("Not yet implemented")
    }

    override fun saveInferenceInitialData(initialData: InitData) {
        TODO("Not yet implemented")
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