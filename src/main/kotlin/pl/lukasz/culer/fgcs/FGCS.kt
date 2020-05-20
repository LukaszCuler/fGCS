package pl.lukasz.culer.fgcs

import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.ClassificationController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.controllers.ParseTreeController
import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.reports.FinalResult
import pl.lukasz.culer.fgcs.models.reports.InitData
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.*
import pl.lukasz.culer.vis.report.ReportsController

class FGCS(val inputSet : List<TestExample>? = null,
           val inputGrammar : Grammar? = null,
           val testSet : List<TestExample>? = null,
           val maxIterations : Int? = null,
           val settings : Settings) {

    //important controllers
    lateinit var grammarController : GrammarController
    lateinit var cykController: CYKController
    lateinit var parseTreeController: ParseTreeController
    lateinit var classificationController: ClassificationController
    lateinit var bestGrammar: Grammar
    private val reportsController = ReportsController(settings.reportsSaverFactory())
    private var perfectionMeasure = Double.MIN_VALUE
    private var iterationNum = 0
    private var simulationStartTime = 0L
    private var bestExamples = listOf<ExampleAnalysisResult>()
    //consts
    companion object {
        const val TAG = "FGCS"
    }
    /**
     * region public methods
     */
    fun inferGrammar(){
        Logger.i(TAG, FGCS_INIT)
        if(!initiateFGCS()) return //no need for inference
        if(inputSet==null) return //should not happen ¯\_(ツ)_/¯

        //if not, let's infer!
        //preprocess input data
        Logger.i(TAG, FGCS_LEARNING_SET_INIT)
        sortInQuasiLexicographicOrder(inputSet)

        //initial assignments
        simulationStartTime = System.currentTimeMillis()
        bestGrammar = grammarController.grammar.copy()
        reportsController.startInference(InitData(inputSet, testSet, maxIterations, settings))
        //iteration loop
        do {
            //changing iteration variables
            val iterationStartTime = System.currentTimeMillis()
            iterationNum++

            //reporting and logging
            Logger.i(TAG, FGCS_ITERATION_START.format(iterationNum))
            reportsController.startIteration(iterationNum)

            //creative process!
            Logger.i(TAG, FGCS_COVERING_STAGE.format(iterationNum))
            inputSet.forEach { parseAndCoverExample(it) }

            //updating all examples with created new rules
            Logger.i(TAG, FGCS_COVERING_PARAMS_REFRESH.format(iterationNum))
            var parsedExamples = RxUtils.computeParallelly(inputSet, ::testExample)
            refreshAttributes(parsedExamples)

            //end-stage - withering and params refresh, if needed
            Logger.i(TAG, FGCS_WITHERING_STAGE.format(iterationNum))
            var ruleRefreshNeeded = witherRules()
            ruleRefreshNeeded = ruleRefreshNeeded || postProcessGrammar()
            if(ruleRefreshNeeded) {
                Logger.i(TAG, FGCS_WITHERING_POSTPROCESS_REFRESH.format(iterationNum))
                refreshAttributes(parsedExamples)
            }

            //clear grammar - consider if needed every iteration
            Logger.i(TAG, FGCS_CLEARING_GRAMMAR.format(iterationNum))
            clearGrammar()

            //performance test before evaluation
            Logger.i(TAG, FGCS_PERFORMANCE_TEST.format(iterationNum))
            parsedExamples = RxUtils.computeParallelly(inputSet, ::testExample)

            //saving best grammar
            val currentMeasure = settings.grammarPerfectionMeasure.getDoubleMeasure(grammarController.grammar, parsedExamples)

            if(currentMeasure >= perfectionMeasure){
                bestGrammar = grammarController.grammar.copy()
                bestExamples = parsedExamples
                perfectionMeasure = currentMeasure
            }

            //iteration end and logging
            val iterationTime = System.currentTimeMillis() - iterationStartTime
            Logger.i(TAG, FGCS_ITERATION_FINISHED.format(iterationNum, perfectionMeasure, iterationTime))
            reportsController.finishIteration(grammarController.grammar, parsedExamples, currentMeasure, iterationTime)

            //iteration can be also interrupted by timeout
        } while((maxIterations!=null && iterationNum<maxIterations)
            && !settings.grammarPerfectionMeasure.isGrammarPerfect(currentMeasure))       //are we perfect yet? ༼ つ ◕_◕ ༽つ

        finalizeInference()
    }

    fun finalizeInference(){
        //ok, inference is done, so we are setting best grammar
        grammarController.grammar = bestGrammar

        //finishing stuff
        val simulationTime = System.currentTimeMillis() - simulationStartTime
        Logger.i(TAG, FGCS_BESTGRAMMAR_FINISHED.format(perfectionMeasure, simulationTime))
        reportsController.finishInference(FinalResult(iterationNum,bestGrammar,bestExamples,perfectionMeasure,simulationTime))
    }

    fun verifyPerformance(){
        //it's time tod face the truth
        if(!::grammarController.isInitialized) return  //something went wrong

        val properTestSet : List<TestExample> = testSet ?: (inputSet ?: return) //ooops...

        val exampleList = RxUtils.computeParallelly(properTestSet, ::testExample)

        for(example in exampleList){
            val fuzzyClass = example.multiParseTreeNode.classificationMembership
            val crispClass = classificationController.getCrispClassification(example.multiParseTreeNode)
            Logger.d(TAG, "${example.example.sequence}: $fuzzyClass - $crispClass")
        }
        reportsController.finishVerification(exampleList)

        //ExamplesHeatmapVisualization(grammarController, classificationController, settings, exampleList).saveToFile("heatmap.html")
    }
    //endregion
    /**
     * region private methods
     */
    private fun sortInQuasiLexicographicOrder(examplesToSort : List<TestExample>) {
        examplesToSort.sortedWith(object : Comparator<TestExample> {
            override fun compare(e1: TestExample, e2: TestExample): Int {
                if(e1.size < e2.size) return -1
                if(e1.size > e2.size) return 1
                for(i in e1.parsedSequence.indices){
                    val symbolComp = e1.parsedSequence[i].symbol.compareTo(e2.parsedSequence[i].symbol)
                    if(symbolComp==0) continue
                    return symbolComp
                }
                return 0
            }
        })
    }

    private fun refreshAttributes(examples :List<ExampleAnalysisResult>) = settings.membershipAssigner.assignMemberships(parseTreeController, grammarController, examples)

    private fun witherRules() = settings.witheringSelector.applyWithering(grammarController)

    private fun postProcessGrammar() : Boolean {
        Logger.i(TAG, FGCS_POSTPROCESSING_STAGE.format(iterationNum))
        return settings.grammarPostProcessor.applyOperators(grammarController)
    }

    private fun clearGrammar(){
        grammarController.removeUnreachableAndUnproductiveRules()
        grammarController.removeUnusedSymbols()
    }

    private fun initiateFGCS() : Boolean{
        //ok we have grammar on input, no need for inference :(
        grammarController = when {
            inputGrammar!=null -> GrammarController(settings, inputGrammar, testData = testSet)
            inputSet==null -> return false //if there is no input grammar and input set - we have nothing to do
            else -> GrammarController(settings, inputSet, testData = testSet)
        }

        //everything is fine, lets rock
        cykController = CYKController(grammarController)
        parseTreeController = ParseTreeController(grammarController, cykController)
        classificationController =  ClassificationController(grammarController, settings)
        grammarController.reportsController = reportsController

        return inputGrammar==null //if there is no input grammar, we have to infer it x]
    }

    private fun parseAndCoverExample(example: TestExample){
        if(example.explicitMembership.midpoint <= settings.coveringThreshold ?: 0.0) return //if below threshold, then we are ignoring
        Logger.d(TAG, FGCS_COVERING_EXAMPLE.format(example.sequence))
        val exampleTable = CYKTable(example)    //we are creating cyk table for example
        cykController.fillCYKTable(exampleTable)
        if(cykController.isExampleParsed(exampleTable)) return //nothing to do here @TODO small chance for creation of additional ones

        settings.coveringFactory(exampleTable, grammarController, cykController, parseTreeController).apply()
    }

    private fun testExample(example: TestExample) : ExampleAnalysisResult{
        val exampleTable = CYKTable(example)    //we are creating cyk table for example
        cykController.fillCYKTable(exampleTable)    //...fill it...
        val parseTree = parseTreeController.getMultiParseTreeFromCYK(exampleTable)  //...create tree using it...
        classificationController.assignClassificationMembership(parseTree) //...and tag it!
        classificationController.assignRelevance(parseTree)                 //assigning relevance

        //ok, so lets collect what we got :)
        return ExampleAnalysisResult(example,
            exampleTable,
            parseTree,
            parseTree.classificationMembership,
            classificationController.getCrispClassification(parseTree))
    }
    //endregion
    /**
     * inner classes
     */
    data class ExampleAnalysisResult(val example: TestExample,
                                     val table : CYKTable,
                                     val multiParseTreeNode: MultiParseTreeNode,
                                     val fuzzyClassification : IntervalFuzzyNumber,
                                     val crispClassification : Boolean)
    //endregion
}