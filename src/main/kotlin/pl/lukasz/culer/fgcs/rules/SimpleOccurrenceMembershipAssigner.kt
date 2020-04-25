package pl.lukasz.culer.fgcs.rules

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.rules.base.MembershipAssigner
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong

class SimpleOccurrenceMembershipAssigner : MembershipAssigner() {
    companion object {
        const val POSITIVE_OCCURED = "POSITIVE_OCCURRED"
        const val NEGATIVE_OCCURED = "NEGATIVE_OCCURRED"
    }

    //extension properties
    fun NRule.initializePositiveOccurred() {
        analysisTemps[POSITIVE_OCCURED] = AtomicLong()
    }

    fun NRule.getPositiveOccurred() = analysisTemps[POSITIVE_OCCURED]

    fun NRule.initializeNegativeOccurred() {
        analysisTemps[NEGATIVE_OCCURED] = AtomicLong()
    }

    fun NRule.getNegativeOccurred() = analysisTemps[NEGATIVE_OCCURED]

    override fun initialize() {
        grammarController?.grammar?.nRules?.forEach {
            it.initializePositiveOccurred()
            it.initializeNegativeOccurred()
        }
    }

    override fun analyzeExample(example: FGCS.ExampleAnalysisResult) {
        parseTreeController?.processRootToNodesAll(example.multiParseTreeNode) {node ->
            node.subtrees.forEach { variant ->
                grammarController?.nRulesWith(node.node, variant.subTreePair.first.node, variant.subTreePair.second.node)
                    ?.single()
                    ?.occurredInParsing
                    ?.set(true)
            }
        }
    }

    override fun finalize() {
        TODO("Not yet implemented")
    }
}