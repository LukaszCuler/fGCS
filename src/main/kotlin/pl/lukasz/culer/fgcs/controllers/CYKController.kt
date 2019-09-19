package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.symbols.NSymbol

class CYKController(val gc: GrammarController) {
    /**
     * region public methods
     */
    fun fillCYKTable(table : CYKTable){
        table.recentlyModified = false //here we go again   //@TODO modify flag

        //we start with terminal rules
        if(table.unitExample
            && gc.tRulesWith(gc.grammar.starSymbol, table.example.parsedSequence[0]).isNotEmpty()) {     //if we have unit example
            table.rootCell.add(gc.grammar.starSymbol)
            return
        }

        //if not unit, it fills terminal rules
        for(i in 0..table.lastIndex){
            val terminalRules = gc.tRulesWith(terminal = table.example.parsedSequence[i])
            var symbolToAdd : NSymbol? = null
            if(terminalRules.size == 1) symbolToAdd = terminalRules.single().left
            else if(terminalRules.size>1) {
                symbolToAdd = terminalRules.firstOrNull { it.left != gc.grammar.starSymbol }?.left
            }
            if(symbolToAdd!=null){
                table.cykTable[0][i].add(symbolToAdd)
            } //else should not happen
        }

        //now the real fun is coming - filling other cells
    }

    fun isExampleParsed(table : CYKTable) = table.rootCell.contains(gc.grammar.starSymbol)

    fun wasTableUpdated(table : CYKTable) = table.recentlyModified
    //endregion

    /**
     * region private, helper methods
     */
    //here be dragons

    //endregion
}