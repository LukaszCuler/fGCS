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
import pl.lukasz.culer.utils.Consts

//@TODO UT
class CompletingCovering : Covering() {
    //region overrides
    override fun apply(
        table: CYKTable,
        grammarController: GrammarController,
        cykController: CYKController,
        parseTreeController: ParseTreeController
    ) {
        //initiating stuff
        val tempRules = table.privateRuleSet
        val tempVars = mutableListOf<NSymbol>()
        tempRules.clear()

        //filling with potential rules
        cykController.doForEveryCell(table) { y: Int, x: Int, cell: CYKCell ->
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

        //creating tree for possible paths
        val parseTree = parseTreeController.getMultiParseTreeFromCYK(table)

        //clearing our mess
        tempRules.clear()
    }
    //endregion
    //region private helpers
    private fun getNewTempValue(last : NSymbol?) = NSymbol(last?.symbol?.let { it+1 } ?: Consts.N_GEN_START_TEMP)
    //endregion
}