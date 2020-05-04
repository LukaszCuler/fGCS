package pl.lukasz.culer.fgcs.models.reports

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.Grammar

class Iteration(val iterationNum : Int) {
    lateinit var grammar : Grammar
    lateinit var analizedExamples : List<FGCS.ExampleAnalysisResult>
    var perfectionMeasure : Double = 0.0
}