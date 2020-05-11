package pl.lukasz.culer.fgcs.models.reports

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.Grammar

data class FinalResult(
    val finalIteration: Int,
    val bestGrammar: Grammar,
    val bestExamples: List<FGCS.ExampleAnalysisResult>,
    val finalMeasure: Double,
    val simulationTime: Long
)