package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.DO_NOT_BELONG_AT_ALL
import pl.lukasz.culer.utils.Consts.Companion.NOT_RELEVANT_AT_ALL

//@TODO unit tests!
class ClassificationController(val gc: GrammarController,
                               val settings: Settings) {
    /**
     * region consts
     */
    private val heatmapProcessor = settings.heatmapProcessorFactory.invoke()
    private val relevanceProcessor = settings.relevanceProcessorFactory.invoke()
    //endregion
    /**
     * region public methods
     */
    fun assignClassificationMembership(parseTree : MultiParseTreeNode) {
        //for leaves we return default membership
        if(parseTree.isLeaf) {
            parseTree.mainMembership = if(parseTree.isDeadEnd) DO_NOT_BELONG_AT_ALL else Consts.T_RULE_MEMBERSHIP
            return
        }

        //now for multiple subtree variants
        for(subtree in parseTree.subtrees){
            //tagging subtrees
            assignClassificationMembership(subtree.subTreePair.first)
            assignClassificationMembership(subtree.subTreePair.second)

            //membership calculation of subtrees and link rule
            subtree.classificationMembership = settings.subtreeMembership(
                subtree.subTreePair.first.mainMembership,
                subtree.subTreePair.second.mainMembership,
                gc.nRulesWith(parseTree.node, subtree.subTreePair.first.node, subtree.subTreePair.second.node).single().membership)
        }

        parseTree.subtrees.sortBy { it.classificationMembership }
        parseTree.mainChild = parseTree.subtrees.last()
        //@TODO should be S-norm
        parseTree.mainMembership = parseTree.subtrees.last().classificationMembership
    }

    fun assignRelevance(parseTree : MultiParseTreeNode) {
        if(parseTree.isLeaf) return
        relevanceProcessor.assignRelevanceToVariants(parseTree.subtrees)

        for(childVariant in parseTree.subtrees){
            assignRelevance(childVariant.subTreePair.first)
            assignRelevance(childVariant.subTreePair.second)
        }
    }

    fun assignDerivationMembership(parseTree : MultiParseTreeNode, inhValue : IntervalFuzzyNumber = DO_NOT_BELONG_AT_ALL, relValue : IntervalFuzzyNumber = NOT_RELEVANT_AT_ALL)
            : MutableList<MutableList<IntervalFuzzyNumber>>{
        if(parseTree.isLeaf) return mutableListOf(mutableListOf(inhValue))  //latter should not happen
        heatmapProcessor.assignDerivationMembershipToVariants(inhValue, parseTree.subtrees, settings)

        var valuesToReturn : MutableList<MutableList<IntervalFuzzyNumber>>? = null

        for(childVariant in parseTree.subtrees){
            val currentValues :  MutableList<MutableList<IntervalFuzzyNumber>>  = mutableListOf()
            currentValues.addAll(assignDerivationMembership(childVariant.subTreePair.first, childVariant.derivationMembership))
            currentValues.addAll(assignDerivationMembership(childVariant.subTreePair.second, childVariant.derivationMembership))
            if(valuesToReturn!=null){
                for(i in 0 until valuesToReturn.size){
                    valuesToReturn[i].addAll(currentValues[i])
                }
            } else valuesToReturn = currentValues
        }
        return valuesToReturn ?: mutableListOf()
    }

    fun getFuzzyClassification(parseTree : MultiParseTreeNode) : IntervalFuzzyNumber {
        return parseTree.mainMembership
    }

    fun getCrispClassification(parseTree : MultiParseTreeNode) : Boolean {
        return parseTree.mainMembership.midpoint >= (settings.crispClassificationThreshold?: 0.0)
    }

    fun getExampleHeatmap(parseTree : MultiParseTreeNode,
                          inhMembership : IntervalFuzzyNumber? = null
    ) /*: MutableList<IntervalFuzzyNumber>*/ {

        //@TODO
        /*//leaf support
        if(parseTree.isLeaf){
            if(inhMembership!=null) return mutableListOf(inhMembership)
            return mutableListOf() //ofc should not happen
        }

        //should not happen ;_;
        val mainSub = parseTree.mainChild ?: return mutableListOf()

        //ok so let's calculate stuff for children
        val myListToReturn : MutableList<IntervalFuzzyNumber> = mutableListOf()

        var newInhValue =
            gc.nRulesWith(parseTree.node, mainSub.subTreePair.first.node, mainSub.subTreePair.second.node).single().membership

        inhMembership?.let {newInhValue = settings.tNorm(it, newInhValue) }

        //@TODO - add S-norm
        myListToReturn.addAll(getExampleHeatmap(mainSub.subTreePair.first, newInhValue))
        myListToReturn.addAll(getExampleHeatmap(mainSub.subTreePair.second, newInhValue))
        return myListToReturn*/
    }
    //endregion
}