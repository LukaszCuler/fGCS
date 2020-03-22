package pl.lukasz.culer.fgcs.covering

import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.controllers.ParseTreeController
import pl.lukasz.culer.fgcs.covering.base.Covering
import pl.lukasz.culer.fgcs.models.CYKCell
import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Logger
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

//@TODO UT
const val TAG = "Completing Covering"

class CompletingCovering(table: CYKTable,
                         grammarController: GrammarController,
                         cykController: CYKController,
                         parseTreeController: ParseTreeController) : Covering(table, grammarController, cykController, parseTreeController) {

    private val tempRules = table.privateRuleSet
    private val tempVars = mutableListOf<NSymbol>()
    private val tags = mutableMapOf<MultiParseTreeNode, Pair<Int, Int>>()
    private val tagsSubtree = mutableMapOf<MultiParseTreeNode.SubTreePair, Pair<Int, Int>>()
    private val possibleConstraintSets = mutableSetOf<ConstraintSet>()
    private var selectedConstraintSet = ConstraintSet()
    private val replacements = mutableMapOf<NSymbol,NSymbol>()

    //region overrides
    override fun apply() {
        //initiating stuff
        tempRules.clear()

        //filling with potential rules
        cykController.doForEveryCell(table, this::addPossibleRules)

        //creating tree for possible paths
        var parseTree = parseTreeController.getMultiParseTreeFromCYK(table)

        //tagging nodes with possible rules to create in descendants
        parseTreeController.processNodesToRoot(parseTree, this::tagWithPossibleNewRules) // remove if frequency not utilized

        //constrains identification, processing and application
        identifyConstraints()
        clusterConstraints()
        assignSymbolsToTemps()

        //updating parsing tree
        cykController.fillCYKTable(table)
        parseTree = parseTreeController.getMultiParseTreeFromCYK(table)
        parseTreeController.processNodesToRoot(parseTree, this::tagWithPossibleNewRules) //tagging nodes with possible rules to create in descendants

        //selecting and adding new rules!
        parseTreeController.processSingleTreeFromRoot(parseTree, this::selectTreeAndRulesToAdd)

        //clearing our mess
        tempRules.clear()
    }
    //endregion
    //region private helpers
    private fun addPossibleRules(y: Int, x: Int, cell: CYKCell){
        //filling only empty cells
        if(cell.isEmpty()){
            val detectors = cykController.findDetectors(table, y, x)

            //obtaining left for new rules
            val leftForRules = if(cell===table.rootCell) grammarController.grammar.starSymbol
            else getNewTempValue(tempVars.lastOrNull()).also { tempVars.add(it) }

            //saving new potential rules
            tempRules.addAll(
                detectors.map { NRule(left = leftForRules, right = NRuleRHS(it.first.symbol, it.second.symbol)) }
            )

            //filling cell with new rule
            cykController.fillCell(table, y, x)
        }
    }

    private fun tagWithPossibleNewRules(processedNode : MultiParseTreeNode){
        if(processedNode.isLeaf){
            tags[processedNode] = 0 to 0
        } else {
            //edge values to tag
            var maxVal = Int.MIN_VALUE
            var minVal = Int.MAX_VALUE

            //going through all variants
            for(sub in processedNode.subtrees){
                var childSumMin = (tags[sub.subTreePair.first]?.first ?: 0) + (tags[sub.subTreePair.second]?.first ?: 0)
                var childSumMax = (tags[sub.subTreePair.first]?.second ?: 0) + (tags[sub.subTreePair.second]?.second ?: 0)

                val constructedRule = getRuleForNodeAndSubtree(processedNode, sub)

                var increase = 0
                if(constructedRule.size > 0 && tempRules.contains(constructedRule.first())) increase = 1

                childSumMin += increase
                childSumMax += increase

                tagsSubtree[sub] = childSumMin to childSumMax

                //how this variant affect?
                maxVal = max(maxVal, childSumMax)
                minVal = min(minVal, childSumMin)
            }

            tags[processedNode] = minVal to maxVal
        }
    }

    private fun getNewTempValue(last : NSymbol?) = NSymbol(last?.symbol?.let { it+1 } ?: Consts.N_GEN_START_TEMP)

    private fun identifyConstraints(){
        //@TODO S->XX
        //is one rule similar to another?
        tempRules.forEach { tempRule ->
            /**
             * first type of analyzed constraints - rules in form of:
             * X1 -> X2A v X1 -> A2X
             * where X1 and X2 are any temporary symbols, and A any existing symbol.
             */
            if(tempVars.contains(tempRule.left) &&
                (tempVars.contains(tempRule.getRightFirst()) xor tempVars.contains(tempRule.getRightSecond()))){
                if(tempVars.contains(tempRule.getRightFirst())){
                    possibleConstraintSets.addAll(getConstraintsForOneOnRight(tempRule,
                        grammarController.nRulesWith(second = tempRule.getRightSecond())
                    ) {it.getRightFirst()})
                } else {
                    possibleConstraintSets.addAll(getConstraintsForOneOnRight(tempRule,
                        grammarController.nRulesWith(second = tempRule.getRightFirst())
                    ) {it.getRightSecond()})
                }
            }
        }
    }

    private fun selectTreeAndRulesToAdd(processedNode: MultiParseTreeNode) : MultiParseTreeNode.SubTreePair? {
        if(processedNode.isLeaf) return null;
        //the smaller possible new rules, the bigger chance to be randomly selected

        //construction of probability tab
        val sum = processedNode.subtrees.sumBy { ((tagsSubtree[it]?.first ?: 0) + (tagsSubtree[it]?.second ?: 0))/2 }
        val probabilityTab = mutableListOf<Pair<Double, MultiParseTreeNode.SubTreePair>>()
        var currentSum = 0.0
        for(sub in processedNode.subtrees){
            currentSum += sum - ((tagsSubtree[sub]?.first ?: 0) + (tagsSubtree[sub]?.second ?: 0))/2
            probabilityTab.add(currentSum to sub)
        }

        //selecting subtree based on probability tab
        val drawnNumber = Random.nextDouble(probabilityTab.last().first)
        var selectedTree : MultiParseTreeNode.SubTreePair? = null

        for(i in probabilityTab.indices){
            if(drawnNumber <= probabilityTab[i].first){
                selectedTree = probabilityTab[i].second
                break
            }
        }
        if(selectedTree==null) return null

        //adding rule associated with subtree
        val constructedRule = getRuleForNodeAndSubtree(processedNode, selectedTree)
        return null
        //if(tempRules.)
        //@TODO should "new" rules be recounted if we add rule based on constraint?
    }

    private fun getConstraintsForOneOnRight(tempRule : NRule, constrainProps : MutableSet<NRule>, getProperSymbol :(NRule) -> NSymbol) : Set<ConstraintSet> {
        val foundSets = mutableSetOf<ConstraintSet>()
        constrainProps
            .map { it.left to getProperSymbol(it)}
            .groupBy { it.first }
            .forEach { foundRule ->
                //first constraint - for left symbol
                val cs = ConstraintSet()
                val leftConstraint = Constraint(tempRule.left, mutableSetOf(foundRule.key))
                cs.constraints.add(leftConstraint)

                //for right - no new constraint for a symbol if the same, just filling the first one
                val rightConstraint = if(tempRule.left==getProperSymbol(tempRule)) leftConstraint
                else Constraint(getProperSymbol(tempRule))
                cs.constraints.add(rightConstraint)
                rightConstraint.right.addAll(foundRule.value.map { it.second })
                foundSets.add(cs)
            }
        return foundSets
    }

    private fun clusterConstraints(){

    }

    private fun assignSymbolsToTemps(){
        //creating replacement table
        selectedConstraintSet.constraints.forEach { constraint ->
            if(constraint.right.size == 1 && tempVars.contains(constraint.right.first())){
                //there was constraint or random value was already created
                var rightReplacement = replacements[constraint.right.first()]
                if(rightReplacement==null){
                    //we need to obtain a random one
                    rightReplacement = grammarController.getNewOrExistingNSymbolRandomly()
                    replacements[constraint.left] = rightReplacement
                }
                replacements[constraint.right.first()] = rightReplacement
            } else {
                replacements[constraint.left] = grammarController.getRandomNSymbol(constraint.right)
            }
        }

        //performing replacement
        val updatedRules : MutableSet<NRule> = mutableSetOf()
        tempRules.forEach {
            val left = replacements[it.left] ?: it.left
            val rightFirst = replacements[it.getRightFirst()] ?: it.getRightFirst()
            val rightSecond = replacements[it.getRightSecond()] ?: it.getRightSecond()
            updatedRules.add(NRule(left, NRuleRHS(rightFirst, rightSecond)))
        }
        tempRules.clear()
        tempRules.addAll(updatedRules)
    }

    private fun getRuleForNodeAndSubtree(node : MultiParseTreeNode, sub: MultiParseTreeNode.SubTreePair) = grammarController.nRulesWith(
        node.node,
        sub.subTreePair.first.node,
        sub.subTreePair.second.node,
        tempRules
    )
    //endregion
    //region internal structures
    /**
     * constraints are in form of equalities linked with conjunction e.g.
     * X1 = (BvCvD) ^ X1=X2
     * first equality type - single temp symbol on the left and multiple on the right
     * second equality type - two single temp symbols on both sides
     * left side should be unique
     * Rhs is assigned to Lhs
     * Type two equalities should be placed at the end
     */
    data class Constraint(val left : NSymbol, val right : MutableSet<NSymbol> = mutableSetOf())
    data class ConstraintSet(val constraints : MutableSet<Constraint> = mutableSetOf())
    //endregion
}