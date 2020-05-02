package pl.lukasz.culer.fgcs.rules.base

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.controllers.ParseTreeController
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.utils.RxUtils
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

abstract class MembershipAssigner {
    var grammarController: GrammarController? = null
    var examples :List<FGCS.ExampleAnalysisResult>? = null
    var parseTreeController: ParseTreeController? = null

    //we have to assign to all at once - due to possible complexity
    fun assignMemberships(parseTreeController: ParseTreeController, grammarController: GrammarController, examples :List<FGCS.ExampleAnalysisResult>){
        this.grammarController = grammarController
        this.examples = examples
        this.parseTreeController = parseTreeController

        initializeAssigner()
        RxUtils.computeParallelly(examples, ::markAndAnalyzeExample)
        finalize()
    }

    private fun markAndAnalyzeExample(example : FGCS.ExampleAnalysisResult){
        //which rules were utilized
        parseTreeController?.processRootToNodesAll(example.multiParseTreeNode) {node ->
            node.subtrees.forEach { variant ->
                grammarController?.nRulesWith(node.node, variant.subTreePair.first.node, variant.subTreePair.second.node)
                    ?.single()
                    ?.occurredInParsing
                    ?.set(true)
            }
        }

        //general stuff implemented, more specific one needed
        analyzeExample(example)
    }

    private fun initializeAssigner(){
        this.grammarController?.grammar?.nRules?.forEach { it.occurredInParsing.set(false) }
        initialize()
    }

    //abstract stuff
    protected abstract fun initialize() //reset, initialize
    protected abstract fun analyzeExample(example : FGCS.ExampleAnalysisResult) //executed parallelly
    protected abstract fun finalize() //if needed
}