package pl.lukasz.culer.fuzzy.processors.relevance

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessor
import pl.lukasz.culer.utils.Consts.Companion.FULL_RELEVANCE
import pl.lukasz.culer.utils.Consts.Companion.NOT_RELEVANT_AT_ALL

/**
 * The only one with the greatest membership is relevant
 */
class WTARelevanceProcessor : RelevanceProcessor {
    override fun assignRelevanceToVariants(children: MutableList<MultiParseTreeNode.SubTreePair>) {
        children.sortBy { it.classificationMembership }
        children.last().relevance = FULL_RELEVANCE      //only greatest one with relevance
        children.filter { it != children.last() }
            .forEach { it.relevance = NOT_RELEVANT_AT_ALL }//others not
    }
}