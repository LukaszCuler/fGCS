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
        const val INFERENCE_END = "tr_inference_end.txt"
        const val ITERATION = "tr_iteration.txt"
        const val GENERIC = "tr_generic.txt"

        const val UNLIMITED_ITERATIONS = "(unlimited)"
        const val RULES_SEPARATOR = ", "
        const val NEW_LINE_SEPARATOR = "\n"
        const val TITLE_SETTINGS = "Settings"
        const val TITLE_INPUT_SET = "Inference Set"
    }
    //endregion
    //region properties
    var initialData: InitData? = null
    //endregion
    override fun initialize(reportName: String) {
        initReport(GENERATED_REPORTS_PATH+reportName+REPORT_EXTENSION,
            getTemplate(START_REPORT)
                .format(reportName))
    }

    override fun saveInferenceInitialData(initialData: InitData) {
        this.initialData = initialData

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
        addToReport(getTemplate(INFERENCE_END).format(
            finalResult.finalIteration,
            finalResult.finalMeasure,
            finalResult.bestGrammar.nRules.joinToString(RULES_SEPARATOR),
            finalResult.bestGrammar.nSymbols.joinToString(RULES_SEPARATOR)
        ))
    }

    override fun finalize() {
/*        addToReport(getTemplate(GENERIC).format(
            TITLE_SETTINGS,
            initialData?.settings.
        ))*/
    }

    override fun saveTestResults(testExamples: List<FGCS.ExampleAnalysisResult>) {
        addToReport(getTemplate(INFERENCE_END).format(
            testExamples.joinToString(NEW_LINE_SEPARATOR) { "${it.example.sequence} | ${it.example.explicitMembership} | ${it.fuzzyClassification} | ${it.crispClassification}" }
        ))
    }
    //region private methods
    private fun getRuleDesc(ruleDesc : Pair<NRule, String>) = "${ruleDesc.first} (${ruleDesc.second})"
    //endregion
}