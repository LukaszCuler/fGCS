package pl.lukasz.culer.fgcs

import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.ClassificationController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.controllers.ParseTreeController
import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
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
        //iteration loop
        do {
            iterationNum++

            //@TODO parallelize ??

            //verification after creation process, base for further operations
            var parsedExamples = RxUtils.computeParallelly(inputSet, ::testExample)

            refreshAttributes()
            witherRules()

            //final performance test //needed?
            parsedExamples = RxUtils.computeParallelly(inputSet, ::testExample)

            //saving best grammar
            if(settings.grammarMeasure.getComparator(parsedExamples).compare(grammarController.grammar, bestGrammar) >= 0){
                bestGrammar = grammarController.grammar.copy()
                bestExamples = parsedExamples
            }

            //iteration can be also interrupted by timeout
        } while((maxIterations!=null && iterationNum<maxIterations)
            || !settings.grammarMeasure.isGrammarPerfect(grammarController.grammar, parsedExamples))       //are we perfect yet? ༼ つ ◕_◕ ༽つ
    }

    fun verifyPerformance(){
        //it's time tod face the truth
        if(!::grammarController.isInitialized) return  //something went wrong

        val properTestSet : List<TestExample> = testSet ?: (inputSet ?: return) //ooops...

        val exampleList = RxUtils.computeParallelly(properTestSet, ::testExample)

        for(example in exampleList){
            val fuzzyClass = classificationController.getFuzzyClassification(example.multiParseTreeNode)
            val crispClass = classificationController.getCrispClassification(example.multiParseTreeNode)
            println("${example.example.sequence}: $fuzzyClass - $crispClass")
        }

        ExamplesHeatmapVisualization(grammarController, classificationController, settings, exampleList).saveToFile("heatmap.html")
    }
    //endregion
    /**
     * region public methods
     */
    private fun parseAndCoverExample(){

    }

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

    private fun testExample(example: TestExample) : ExampleAnalysisResult{
        val exampleTable = CYKTable(example)    //we are creating cyk table for example
        cykController.fillCYKTable(exampleTable)    //...fill it...
        val parseTree = parseTreeController.getMultiParseTreeFromCYK(exampleTable)  //...create tree using it...
        classificationController.assignClassificationMembership(parseTree) //...and tag it!
        classificationController.assignRelevance(parseTree)                 //assigning relevance

        //ok, so lets collect what we got :)
        return ExampleAnalysisResult(example, exampleTable, parseTree)
    }
    //endregion
    /**
     * inner classes
     */
    data class ExampleAnalysisResult(val example: TestExample, val table : CYKTable, val multiParseTreeNode: MultiParseTreeNode)
    //endregion
}