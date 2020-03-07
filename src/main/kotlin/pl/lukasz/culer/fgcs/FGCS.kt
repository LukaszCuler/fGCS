package pl.lukasz.culer.fgcs

import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.ClassificationController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.controllers.ParseTreeController
import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.RxUtils
import pl.lukasz.culer.vis.heatmap.ExamplesHeatmapVisualization

//@TODO fill UT
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
    /**
     * region public methods
     */
    fun inferGrammar(){
        if(!initiateFGCS()) return //no need for inference
        if(inputSet==null) return //should not happen ¯\_(ツ)_/¯

        //if not, let's infer!
        var iterationNum = 0

        var bestGrammar = grammarController.grammar.copy()
        var bestExamples = listOf<ExampleAnalysisResult>()
        var perfectionMeasure = Double.MIN_VALUE

        //@TODO sort in lexical order?
        //iteration loop
        do {
            iterationNum++

            //@TODO parallelize ??
            //creative process!
            inputSet.forEach { parseAndCoverExample(it) }

            //updating all examples with created new rules
            var parsedExamples = RxUtils.computeParallelly(inputSet, ::testExample)

            refreshAttributes()
            witherRules()

            //performance test before evaluation
            parsedExamples = RxUtils.computeParallelly(inputSet, ::testExample)

            //saving best grammar
            val currentMeasure = settings.grammarPerfectionMeasure.getDoubleMeasure(grammarController.grammar, parsedExamples)

            if(currentMeasure >= perfectionMeasure){
                bestGrammar = grammarController.grammar.copy()
                bestExamples = parsedExamples
                perfectionMeasure = currentMeasure
            }

            //iteration can be also interrupted by timeout
        } while((maxIterations!=null && iterationNum<maxIterations)
            || !settings.grammarPerfectionMeasure.isGrammarPerfect(currentMeasure))       //are we perfect yet? ༼ つ ◕_◕ ༽つ

        //ok, inference is done, so we are setting best grammar
        grammarController.grammar = bestGrammar
    }

    fun verifyPerformance(){
        //it's time tod face the truth
        if(!::grammarController.isInitialized) return  //something went wrong

        val properTestSet : List<TestExample> = testSet ?: (inputSet ?: return) //ooops...

        val exampleList = RxUtils.computeParallelly(properTestSet, ::testExample)

        for(example in exampleList){
            val fuzzyClass = example.multiParseTreeNode.classificationMembership
            val crispClass = classificationController.getCrispClassification(example.multiParseTreeNode)
            println("${example.example.sequence}: $fuzzyClass - $crispClass")
        }

        ExamplesHeatmapVisualization(grammarController, classificationController, settings, exampleList).saveToFile("heatmap.html")
    }
    //endregion
    /**
     * region public methods
     */
    private fun refreshAttributes(){
        //refreshes rules
    }

    private fun witherRules(){

    }

    private fun initiateFGCS() : Boolean{
        //ok we have grammar on input, no need for inference :(
        grammarController = when {
            inputGrammar!=null -> GrammarController(inputGrammar, testData = testSet)
            inputSet==null -> return false //if there is no input grammar and input set - we have nothing to do
            else -> GrammarController(inputSet, testData = testSet)
        }

        //everything is fine, lets rock
        cykController = CYKController(grammarController)
        parseTreeController = ParseTreeController(grammarController, cykController)
        classificationController =  ClassificationController(grammarController, settings)

        return inputGrammar==null //if there is no input grammar, we have to infer it x]
    }

    //@TODO UT
    private fun parseAndCoverExample(example: TestExample){
        val exampleTable = CYKTable(example)    //we are creating cyk table for example
        if(cykController.isExampleParsed(exampleTable)) return //nothing to do here @TODO small chance for creation of additional ones

        settings.covering.apply(exampleTable, grammarController, cykController, parseTreeController)
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