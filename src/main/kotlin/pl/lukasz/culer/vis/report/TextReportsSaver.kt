package pl.lukasz.culer.vis.report

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.reports.FinalResult
import pl.lukasz.culer.fgcs.models.reports.InitData
import pl.lukasz.culer.fgcs.models.reports.Iteration
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.utils.Consts.Companion.GENERATED_REPORTS_PATH
import pl.lukasz.culer.utils.TextReport
import pl.lukasz.culer.vis.report.base.ReportsSaver

class TextReportsSaver : ReportsSaver, TextReport() {
    //region consts
    companion object {
        const val REPORT_EXTENSION = ".txt"

        const val START_REPORT = "tr_start.txt"
        const val INFERENCE_START = "tr_inference_start.txt"
        const val ITERATION = "tr_iteration.txt"

        const val UNLIMITED_ITERATIONS = "(unlimited)"
        const val RULES_SEPARATOR = ", "
    }
    //endregion
    override fun initialize(reportName: String) {
        initReport(GENERATED_REPORTS_PATH+reportName+REPORT_EXTENSION,
            getTemplate(START_REPORT)
                .format(reportName))
    }

    override fun saveInferenceInitialData(initialData: InitData) {
        addToReport(getTemplate(INFERENCE_START).format(
            initialData.inputSet.size,
            initialData.maxIterations?.toString() ?: UNLIMITED_ITERATIONS
        ))
    }

    override fun saveIteration(iteration: Iteration) {
        val remainingRules = mutableListOf<NRule>()
        remainingRules.addAll(iteration.grammar.nRules)
        remainingRules.removeAll(iteration.addedRules.map { it.first })
        remainingRules.removeAll(iteration.removedRules.map { it.first })

        val remainingSymbols = mutableListOf<NSymbol>()
        remainingSymbols.addAll(iteration.grammar.nSymbols)
        remainingSymbols.removeAll(iteration.addedSymbols.map { it.first })
        remainingSymbols.removeAll(iteration.removedSymbols.map { it.first })

        addToReport(getTemplate(ITERATION).format(
            iteration.iterationNum,
            iteration.perfectionMeasure,
            iteration.addedRules.joinToString(RULES_SEPARATOR) { getRuleDesc(it) },
            remainingRules.joinToString(RULES_SEPARATOR),
            iteration.removedRules.joinToString(RULES_SEPARATOR) { getRuleDesc(it) },
            iteration.addedSymbols.joinToString(RULES_SEPARATOR),
            remainingSymbols.joinToString(RULES_SEPARATOR),
            iteration.removedSymbols.joinToString(RULES_SEPARATOR)
        ))
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
    //region private methods
    private fun getRuleDesc(ruleDesc : Pair<NRule, String>) = "${ruleDesc.first} (${ruleDesc.second})"
    //endregion
}