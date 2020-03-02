package pl.lukasz.culer.fuzzy.processors.heatmap

import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.base.SymbolDerivativeData
import pl.lukasz.culer.fuzzy.processors.heatmap.base.SymbolDerivativeMembership
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts.Companion.DO_NOT_BELONG_AT_ALL
import pl.lukasz.culer.utils.Consts.Companion.FULL_RELEVANCE
import pl.lukasz.culer.utils.Consts.Companion.NOT_RELEVANT_AT_ALL


class MaxMembershipHeatmapProcessor : HeatmapProcessor {
    override fun assignDerivationMembershipToVariants(grammarController: GrammarController,
                                                      inhValue : IntervalFuzzyNumber,
                                                      relValue : IntervalFuzzyNumber,
                                                      parseTreeNode: MultiParseTreeNode,
                                                      settings: Settings) {
        if(inhValue.midpoint == 0.0) return

        var bestChild : MultiParseTreeNode.SubTreePair? = null
        var bestValue : IntervalFuzzyNumber? = null
        for(child in parseTreeNode.subtrees){
            val appliedRuleMembership =
                grammarController.nRulesWith(parseTreeNode.node, child.subTreePair.first.node, child.subTreePair.second.node).single().membership
            if(bestValue==null || appliedRuleMembership > bestValue) {
                bestChild = child
                bestValue = appliedRuleMembership
            }
            child.derivationRelevance = NOT_RELEVANT_AT_ALL
            child.derivationMembership = DO_NOT_BELONG_AT_ALL
        }
        if(bestValue!=null&&bestChild!=null){
            bestChild.derivationMembership = settings.tOperatorRev(arrayOf(inhValue, bestValue))
            bestChild.derivationRelevance = FULL_RELEVANCE
        }
    }

    override fun assignValueToSymbol(symbolValues: List<SymbolDerivativeData>) : SymbolDerivativeMembership
            = symbolValues.map { it.first }.max() ?: DO_NOT_BELONG_AT_ALL

    override fun mainTreeDistinguishable(): Boolean = false

    override fun getMainTree(parseTreeNode: MultiParseTreeNode, grammarController: GrammarController): MultiParseTreeNode.SubTreePair?
            = parseTreeNode.subtrees.maxBy {
            grammarController.nRulesWith(parseTreeNode.node, it.subTreePair.first.node, it.subTreePair.second.node).single().membership
        }

    override fun showAllSubtrees(): Boolean = false
}