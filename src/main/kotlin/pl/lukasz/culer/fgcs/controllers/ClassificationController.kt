package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.DO_NOT_BELONG_AT_ALL
import pl.lukasz.culer.utils.Consts.Companion.FULL_MEMBERSHIP
import pl.lukasz.culer.utils.Consts.Companion.FULL_RELEVANCE

class ClassificationController(val gc: GrammarController,
                               val settings: Settings) {
    /**
     * region consts
     */

    //endregion
    /**
     * region public methods
     */
    fun assignClassificationMembership(parseTree : MultiParseTreeNode) {
        //for multiple subtree variants
        for(subtree in parseTree.subtrees){
            //tagging subtrees
            assignClassificationMembership(subtree.subTreePair.first)
            assignClassificationMembership(subtree.subTreePair.second)

            //membership calculation of subtrees and link rule
            subtree.classificationMembership = settings.tOperatorReg(
                getFuzzyClassification(subtree.subTreePair.first),
                getFuzzyClassification(subtree.subTreePair.second),
                gc.nRulesWith(parseTree.node, subtree.subTreePair.first.node, subtree.subTreePair.second.node).single().membership)
        }
    }

    fun assignRelevance(parseTree : MultiParseTreeNode) {
        if(parseTree.isLeaf) return
        settings.relevanceProcessor.assignRelevanceToVariants(parseTree.subtrees)

        for(childVariant in parseTree.subtrees){
            assignRelevance(childVariant.subTreePair.first)
            assignRelevance(childVariant.subTreePair.second)
        }
    }

    fun assignDerivationMembership(parseTree : MultiParseTreeNode, inhValue : IntervalFuzzyNumber = FULL_MEMBERSHIP, relValue : IntervalFuzzyNumber = FULL_RELEVANCE)
            : MutableList<MutableList<Pair<IntervalFuzzyNumber, IntervalFuzzyNumber>>>{
        if(parseTree.isLeaf || inhValue == DO_NOT_BELONG_AT_ALL) return mutableListOf(mutableListOf(Pair(inhValue, relValue)))
        settings.heatmapProcessor.assignDerivationMembershipToVariants(gc, inhValue, relValue, parseTree, settings)

        var valuesToReturn : MutableList<MutableList<Pair<IntervalFuzzyNumber, IntervalFuzzyNumber>>>? = null

        for(childVariant in parseTree.subtrees){
            val currentValues :  MutableList<MutableList<Pair<IntervalFuzzyNumber, IntervalFuzzyNumber>>>  = mutableListOf()
            currentValues.addAll(assignDerivationMembership(childVariant.subTreePair.first, childVariant.derivationMembership, childVariant.derivationRelevance))
            currentValues.addAll(assignDerivationMembership(childVariant.subTreePair.second, childVariant.derivationMembership, childVariant.derivationRelevance))
            if(valuesToReturn!=null){
                for(i in 0 until valuesToReturn.size){
                    valuesToReturn[i].addAll(currentValues[i])
                }
            } else valuesToReturn = currentValues
        }
        return valuesToReturn ?: mutableListOf()
    }

    fun getFuzzyClassification(parseTree : MultiParseTreeNode) : IntervalFuzzyNumber {
        //for leaves we return default membership
        if(parseTree.isLeaf) {
            return if(parseTree.isDeadEnd) DO_NOT_BELONG_AT_ALL else Consts.T_RULE_MEMBERSHIP
        }

        return settings.sOperatorReg(
            parseTree.subtrees.map { it.classificationMembership }.toTypedArray()
        )

    }

    fun getCrispClassification(parseTree : MultiParseTreeNode) : Boolean {
        return getFuzzyClassification(parseTree).midpoint >= (settings.crispClassificationThreshold?: 0.0)
    }

    fun getExampleHeatmap(parseTree : MultiParseTreeNode) : List<IntervalFuzzyNumber> {
        val membershipsList = assignDerivationMembership(parseTree)
            .map { settings.heatmapProcessor.assignValueToSymbol(it) }
            .toMutableList()

        //uniform 'n' upper eq
        val uniqueList = membershipsList.toSet().toList().sorted()
        val maxItem = uniqueList.maxBy { it.midpoint }?.midpoint
        val minItem = uniqueList.minBy { it.midpoint }?.midpoint

        if(maxItem!=null&&minItem!=null&&maxItem!=minItem){
            val diff = (1.0-minItem)/(uniqueList.size-1)
            for(i in membershipsList.indices){
                val idx = uniqueList.indexOf(membershipsList[i])
                membershipsList[i] = F(minItem+idx*diff)
            }
        }
        return membershipsList
    }
    //endregion
}