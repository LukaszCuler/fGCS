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
import kotlin.math.max
import kotlin.math.min

//@TODO UT
class CompletingCovering(table: CYKTable,
                         grammarController: GrammarController,
                         cykController: CYKController,
                         parseTreeController: ParseTreeController) : Covering(table, grammarController, cykController, parseTreeController) {

    private val tempRules = table.privateRuleSet
    private val tempVars = mutableListOf<NSymbol>()
    private val tags = mutableMapOf<MultiParseTreeNode, Pair<Int, Int>>()
    private val possibleConstraintSets = mutableSetOf<ConstraintSet>()
    private var selectedConstraintSet = ConstraintSet()

    //region overrides
    override fun apply() {
        //initiating stuff
        tempRules.clear()

        //filling with potential rules
        cykController.doForEveryCell(table, this::addPossibleRules)

        //creating tree for possible paths
        val parseTree = parseTreeController.getMultiParseTreeFromCYK(table)

        //tagging nodes with possible rules to create in descendants
        parseTreeController.processNodesToRoot(parseTree, this::tagWithPossibleNewRules)

        identifyConstraints()
        clusterConstraints()

        assignSymbolsToTemps()

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

                val constructedRule = grammarController.nRulesWith(
                    processedNode.node,
                    sub.subTreePair.first.node,
                    sub.subTreePair.second.node,
                    tempRules
                )

                var increase = 0
                if(constructedRule.size > 0 && tempRules.contains(constructedRule.first())) increase = 1

                childSumMin += increase
                childSumMax += increase

                //how this variant affect?
                maxVal = max(maxVal, childSumMax)
                minVal = min(minVal, childSumMin)
            }

            tags[processedNode] = minVal to maxVal
        }
    }

    private fun getNewTempValue(last : NSymbol?) = NSymbol(last?.symbol?.let { it+1 } ?: Consts.N_GEN_START_TEMP)

    private fun identifyConstraints(){
        //is one rule similar to another?
        tempRules.forEach { tempRule ->
            /**
             * first type of analyzed constraints - rules in form of:
             * X1 -> X2A v X1 -> A2X
             * where X1 and X2 are any temporary symbols, and A any existing symbol .
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

    }
    //endregion
    //region internal structures
    /**
     * constraints are in form of equalities linked with conjunction e.g.
     * A = (BvCvD) ^ E=F
     */
    data class Constraint(val left : NSymbol, val right : MutableSet<NSymbol> = mutableSetOf())
    data class ConstraintSet(val constraints : MutableSet<Constraint> = mutableSetOf())
    //endregion
}