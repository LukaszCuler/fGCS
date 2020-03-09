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
    private val constraintSets = mutableSetOf<ConstraintSet>()

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
            if(tempVars.contains(tempRule.getRightFirst()) xor tempVars.contains(tempRule.getRightSecond())){
                if(tempVars.contains(tempRule.getRightFirst())){
                    val constrainProps = grammarController.nRulesWith(second = tempRule.getRightSecond())
                    val sets = constrainProps
                        .map { it.left to it.getRightFirst()}
                        .groupBy { it.first }
                        .map { foundRule ->
                            ConstraintSet(mutableListOf(Constraint(foundRule.key, foundRule.value.map { it.second }.toMutableList()))) }
                    constraintSets.addAll(sets)
                } else {
                    val constrainProps = grammarController.nRulesWith(first = tempRule.getRightFirst())
                    val sets = constrainProps
                        .map { it.left to it.getRightSecond()}
                        .groupBy { it.first }
                        .map { foundRule ->
                            ConstraintSet(mutableListOf(Constraint(foundRule.key, foundRule.value.map { it.second }.toMutableList()))) }
                    constraintSets.addAll(sets)
                }
            }
        }
    }

    private fun clusterConstraints(){

    }
    //endregion
    //region internal structures
    /**
     * constraints are in form of equalities linked with conjunction e.g.
     * A = (BvCvD) ^ E=F
     */
    data class Constraint(val left : NSymbol, val right : MutableList<NSymbol> = mutableListOf())
    data class ConstraintSet(val constraints : MutableList<Constraint> = mutableListOf())
    //endregion
}