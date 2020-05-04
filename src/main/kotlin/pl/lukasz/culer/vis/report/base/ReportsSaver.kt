package pl.lukasz.culer.vis.report.base

import pl.lukasz.culer.fgcs.models.reports.FinalResult
import pl.lukasz.culer.fgcs.models.reports.InitData
import pl.lukasz.culer.fgcs.models.reports.Iteration

interface ReportsSaver {
    fun initialize(reportName : String)
    fun saveInitialData(initialData: InitData)
    fun saveIteration(iteration : Iteration)
    fun saveFinalData(finalResult: FinalResult)
    fun finalize()
}