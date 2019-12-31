package pl.lukasz.culer.fuzzy.processors.relevance

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessor
import pl.lukasz.culer.utils.Consts.Companion.FULL_RELEVANCE
import pl.lukasz.culer.utils.Consts.Companion.NOT_RELEVANT_AT_ALL

/**
 * Variant with highest membership is baseline - max relevance
 */
class MembershipProportionalRelevanceProcessor : RelevanceProcessor {
    override fun assignRelevanceToVariants(children: MutableList<MultiParseTreeNode.SubTreePair>) {
        //first we find the greatest value
        children.sortBy { it.classificationMembership }
        val maxItem = children.maxBy { it.classificationMembership }?.classificationMembership?.midpoint ?: 0.0
        val minItem = children.minBy { it.classificationMembership }?.classificationMembership?.midpoint ?: 0.0

        if(maxItem==minItem) {
            for(variant in children){
                variant.relevance = if(maxItem == 0.0) NOT_RELEVANT_AT_ALL else FULL_RELEVANCE
            }
        } else {
            val coef = 1.0/(maxItem-minItem)
            for(variant in children){
                variant.relevance = variant.classificationMembership - minItem
                variant.relevance = variant.relevance * coef
            }
        }
    }
}