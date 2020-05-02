package pl.lukasz.culer.fgcs.models.reports

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.Grammar

data class Iteration(
    val grammar : Grammar,
    val analizedExamples : List<FGCS.ExampleAnalysisResult>,
    val perfectionMeasure : Double,
    val iterationNum : Int)