package pl.lukasz.culer.fgcs

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
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

class FGCS(val inputSet : List<TestExample>? = null,
           val inputGrammar : Grammar? = null,
           val testSet : List<TestExample>? = null,
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
        if(!initiateFGCS()) return //no need for interence

        //@TODO :)
    }

    fun verifyPerformance(){
        //it's time to face the truth
        if(!::grammarController.isInitialized) return  //something went wrong

        val properTestSet : List<TestExample> = testSet ?: (inputSet ?: return) //ooops...

        //since we want multithreading, we need to do some initial work
        Observable.zip(properTestSet.map { example ->
            Observable.create(ObservableOnSubscribe<ExampleAnalysisResult> {
                it.onNext(testExample(example))
                it.onComplete()
            })
        }.toList()) { resultsArray ->
            resultsArray.map { it as ExampleAnalysisResult }.toList()
        }.subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.trampoline())
            .subscribe(object : Observer<List<ExampleAnalysisResult>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(exampleList: List<ExampleAnalysisResult>) {
                    //visualize
                }

                override fun onError(e: Throwable) {
                }

            })

    }
    //endregion
    /**
     * region public methods
     */
    private fun initiateFGCS() : Boolean{
        //ok we have grammar on input, no need for inference :(
        if(inputGrammar!=null) {
            grammarController = GrammarController(inputGrammar)
            return false //no need for inference
        }

        //if there is no input grammar and input set - we have nothing to do
        if(inputSet==null) return false

        //everything is fine, lets rock
        grammarController = GrammarController(inputSet)
        cykController = CYKController(grammarController)
        parseTreeController = ParseTreeController(grammarController, cykController)
        classificationController =  ClassificationController(grammarController, settings)
        return true
    }

    private fun testExample(example: TestExample) : ExampleAnalysisResult{
        val exampleTable = CYKTable(example)    //we are creating cyk table for example
        cykController.fillCYKTable(exampleTable)    //...fill it...
        val parseTree = parseTreeController.getMultiParseTreeFromCYK(exampleTable)  //...create tree using it...
        classificationController.tagTree(parseTree) //...and tag it!

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