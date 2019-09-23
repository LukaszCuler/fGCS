package pl.lukasz.culer.fgcs

import TreeNode
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import pl.lukasz.culer.data.ProcessDataLoader
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.ClassificationController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.controllers.ParseTreeController
import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.settings.Settings
import java.io.File

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
        val exampleList = Single.zip(properTestSet.map { example ->
            Single.create(SingleOnSubscribe<ExampleAnalysisResult> {
                it.onSuccess(testExample(example))
            })
        }.toList()) { resultsArray ->
            resultsArray.map { it as ExampleAnalysisResult }.toList()
        }.subscribeOn(Schedulers.computation()).blockingGet()

        for(example in exampleList){
            val fuzzyClass = classificationController.getFuzzyClassification(example.multiParseTreeNode)
            val crispClass = classificationController.getCrispClassification(example.multiParseTreeNode)
            println("${example.example.sequence}: $fuzzyClass - $crispClass")
        }

        saveVis("heatmap.html", exampleList)
    }
    //endregion
    /**
     * region public methods
     */
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
        return inputGrammar==null //@TODO make this look better, please
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

    //@TODO TO REMOVE
    fun saveVis(filePath : String, exampleList : List<ExampleAnalysisResult>){
        var html = "<html><body>Membership | Crisp classification | Example<br><br>"

        for(example in exampleList){
            val fuzzyClass = classificationController.getFuzzyClassification(example.multiParseTreeNode)
            val crispClass = classificationController.getCrispClassification(example.multiParseTreeNode)
            val exampleHeatmap = classificationController.getExampleHeatmap(example.multiParseTreeNode)

            var exampleString = "${"%.2f".format(fuzzyClass.midpoint)} | $crispClass | "

            if(example.multiParseTreeNode.isDeadEnd){
                exampleString += "<font color='#ff0000'>${example.example.sequence}</font>"
            } else {
                for(i in 0 until example.example.size){
                    val colorR = (255*(1.0-exampleHeatmap[i].midpoint)).toInt()
                    val colorG = (255*exampleHeatmap[i].midpoint).toInt()
                    val hexR = "%02x".format(colorR)
                    val hexG = "%02x".format(colorG)

                    exampleString += "<font color='#$hexR${hexG}00'>${example.example.sequence[i]}</font>"
                }
            }
            html += "$exampleString<br>"

            if(!example.multiParseTreeNode.isDeadEnd){
                val root = getNode(example.multiParseTreeNode)
                html+= "<tt>$root</tt><br><br>"
            }
        }
        html+="</body></html>"
        File(filePath).writeText(html)
    }

    fun getNode(tree : MultiParseTreeNode) : TreeNode {
        val myNode = TreeNode(tree.node.symbol.toString())
        myNode.memb = tree.mainMembership.midpoint;
        if(tree.isLeaf){
            myNode.children.add(TreeNode(grammarController.tRulesWith(left = tree.node).single().getRight().symbol.toString()).apply { memb = tree.mainMembership.midpoint })
        } else {
            myNode.children.add(getNode(tree.mainChild!!.subtrees.first))
            myNode.children.add(getNode(tree.mainChild!!.subtrees.second))
        }
        return myNode
    }
}