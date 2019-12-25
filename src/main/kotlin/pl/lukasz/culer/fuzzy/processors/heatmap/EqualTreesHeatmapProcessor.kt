package pl.lukasz.culer.fuzzy.processors.heatmap

import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.DO_NOT_BELONG_AT_ALL
import pl.lukasz.culer.utils.Consts.Companion.FULL_RELEVANCE

/**
 * @TODO unit tests!
 */
class EqualTreesHeatmapProcessor : HeatmapProcessor {
    override fun assignDerivationMembershipToVariants(grammarController: GrammarController,
                                                      inhValue : IntervalFuzzyNumber,
                                                      relValue : IntervalFuzzyNumber,
                                                      parseTreeNode: MultiParseTreeNode,
                                                      settings: Settings) {
        if(inhValue.midpoint == 0.0) return
        for(child in parseTreeNode.subtrees){
            val appliedRuleMembership =
                grammarController.nRulesWith(parseTreeNode.node, child.subTreePair.first.node, child.subTreePair.second.node).single().membership
            child.derivationMembership = settings.tNorm(inhValue, appliedRuleMembership)
            child.derivationRelevance = FULL_RELEVANCE
        }
    }

    override fun assignValueToSymbol(symbolValues: List<Pair<IntervalFuzzyNumber, IntervalFuzzyNumber>>) : IntervalFuzzyNumber {
        val sumOfWeightedMemberships = symbolValues.map { it.first }.reduce { first, second -> first + second }

        //mean
        return sumOfWeightedMemberships / symbolValues.size.toDouble()
    }

    override fun mainTreeDistinguishable(): Boolean = false

    override fun getMainTree(parseTreeNode: MultiParseTreeNode, grammarController: GrammarController): MultiParseTreeNode.SubTreePair? = null

    override fun showAllSubtrees(): Boolean = false
}