package pl.lukasz.culer.fuzzy.processors.heatmap

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.DO_NOT_BELONG_AT_ALL

/**
 * @TODO unit tests!
 */
class MaxMembershipHeatmapProcessor : HeatmapProcessor {
    override fun assignDerivationMembershipToVariants(inhValue : IntervalFuzzyNumber,
                                                      relValue : IntervalFuzzyNumber,
                                                      children: List<MultiParseTreeNode.SubTreePair>,
                                                      settings: Settings) {
        if(inhValue.midpoint == 0.0) return

        var bestChild : MultiParseTreeNode.SubTreePair? = null
        for(child in children){
            if(bestChild==null || child.classificationMembership.midpoint > bestChild.classificationMembership.midpoint) {
                bestChild = child
            }
        }
        if(bestChild!=null){
            bestChild.derivationMembership = bestChild.classificationMembership
            settings.tNorm(inhValue, bestChild.derivationMembership)
        }
    }

    override fun assignValueToSymbol(symbolValues: List<Pair<IntervalFuzzyNumber, IntervalFuzzyNumber>>) : IntervalFuzzyNumber
            = symbolValues.map { it.first }.max() ?: DO_NOT_BELONG_AT_ALL

    override fun mainTreeDistinguishable(): Boolean = true

    override fun getMainTree(children: List<MultiParseTreeNode.SubTreePair>): MultiParseTreeNode.SubTreePair?
            = children.maxBy { it.classificationMembership }


}