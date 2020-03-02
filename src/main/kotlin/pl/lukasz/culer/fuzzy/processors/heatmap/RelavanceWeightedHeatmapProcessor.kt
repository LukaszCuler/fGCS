package pl.lukasz.culer.fuzzy.processors.heatmap

import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.settings.Settings

class RelavanceWeightedHeatmapProcessor : HeatmapProcessor {
    override fun assignDerivationMembershipToVariants(grammarController: GrammarController,
                                                      inhValue : IntervalFuzzyNumber,
                                                      relValue : IntervalFuzzyNumber,
                                                      parseTreeNode: MultiParseTreeNode,
                                                      settings: Settings) {
        if(inhValue.midpoint == 0.0) return
        for(child in parseTreeNode.subtrees){
            val appliedRuleMembership =
                grammarController.nRulesWith(parseTreeNode.node, child.subTreePair.first.node, child.subTreePair.second.node).single().membership
            child.derivationMembership = settings.tOperatorRev(arrayOf(inhValue, appliedRuleMembership))
            child.derivationRelevance = settings.tOperatorRev(arrayOf(relValue, child.relevance))
        }
    }

    override fun assignValueToSymbol(symbolValues: List<Pair<IntervalFuzzyNumber, IntervalFuzzyNumber>>) : IntervalFuzzyNumber {
        val sumOfWeightedMemberships = symbolValues.map { it.first * it.second }.reduce { first, second -> first + second }
        val sumOfRelevance = symbolValues.map { it.second }.reduce { first, second -> first + second }

        //weighted mean
        return sumOfWeightedMemberships / sumOfRelevance
    }

    override fun mainTreeDistinguishable(): Boolean = false

    override fun getMainTree(parseTreeNode: MultiParseTreeNode, grammarController: GrammarController): MultiParseTreeNode.SubTreePair? = null

    override fun showAllSubtrees(): Boolean = false
}