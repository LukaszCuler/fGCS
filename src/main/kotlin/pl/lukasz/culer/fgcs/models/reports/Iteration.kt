package pl.lukasz.culer.fgcs.models.reports

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.utils.Consts

class Iteration(val iterationNum : Int) {
    var grammar : Grammar = Grammar()
    var analizedExamples : List<FGCS.ExampleAnalysisResult> = listOf()
    var perfectionMeasure : Double = Consts.DO_NOT_BELONG_AT_ALL.midpoint

    var addedRules : MutableList<Pair<NRule,String>> = mutableListOf()
    var removedRules : MutableList<Pair<NRule,String>> = mutableListOf()
}