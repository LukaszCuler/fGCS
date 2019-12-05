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
class RelavanceWeightedHeatmapProcessor : HeatmapProcessor {
    override fun assignDerivationMembershipToVariants(inhValue : IntervalFuzzyNumber,
                                                      relValue : IntervalFuzzyNumber,
                                                      children: List<MultiParseTreeNode.SubTreePair>,
                                                      settings: Settings) {
        if(inhValue.midpoint == 0.0) return
        for(child in children){
            child.derivationMembership = settings.tNorm(inhValue, child.classificationMembership)
        }
    }

    override fun assignValueToSymbol(symbolValues: List<Pair<IntervalFuzzyNumber, IntervalFuzzyNumber>>) : IntervalFuzzyNumber {
        val sumOfMemberships = symbolValues.map { it.first }.reduce { first, second -> first + second }
        val sumOfRelevance = symbolValues.map { it.second }.reduce { first, second -> first + second }

        //weighted mean
        return sumOfMemberships / sumOfRelevance
    }

    override fun mainTreeDistinguishable(): Boolean = false

    override fun getMainTree(children: List<MultiParseTreeNode.SubTreePair>): MultiParseTreeNode.SubTreePair?
            = null


}