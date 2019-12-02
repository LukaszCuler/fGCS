package pl.lukasz.culer.fuzzy.processors.heatmap

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.settings.Settings

/**
 * @TODO unit tests!
 */
class MaxMembershipHeatmapProcessor : HeatmapProcessor {
    override fun assignDerivationMembershipToVariants(inhValue : IntervalFuzzyNumber?,
                                                      children: MutableList<MultiParseTreeNode.SubTreePair>,
                                                      settings: Settings) {
        if(inhValue!=null && inhValue.midpoint == 0.0) return

        var bestChild : MultiParseTreeNode.SubTreePair? = null
        for(child in children){
            if(bestChild==null || child.classificationMembership.midpoint > bestChild.classificationMembership.midpoint) {
                bestChild = child
            }
        }
        if(bestChild!=null){
            bestChild.derivationMembership = bestChild.classificationMembership
            if(inhValue!=null) settings.tNorm(inhValue, bestChild.derivationMembership)
        }
    }
    
    
}