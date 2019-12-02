package pl.lukasz.culer.fuzzy.processors.relevance

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessor
import pl.lukasz.culer.utils.Consts.Companion.NOT_RELEVANT_AT_ALL

/**
 * Variant with highest membership is baseline - max relevance
 * @TODO unit tests!
 */
class MembershipProportionalRelevanceProcessor : RelevanceProcessor {
    override fun assignRelevanceToVariants(children: MutableList<MultiParseTreeNode.SubTreePair>) {
        //first we find the greatest value
        children.sortBy { it.classificationMembership }
        val maxValue = children.last().classificationMembership.midpoint

        if(maxValue != 0.0){       //we have to be protected in case of strange things
            for(variant in children){
                variant.relevance = IntervalFuzzyNumber(variant.classificationMembership.midpoint / maxValue)
            }
        } else {
            for(variant in children){
                variant.relevance = NOT_RELEVANT_AT_ALL
            }
        }
    }
}