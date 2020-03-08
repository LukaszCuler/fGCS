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
    //endregion
}