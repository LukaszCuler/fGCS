package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.symbols.NSymbol

//typealiases linked with CYK
typealias Detectors = MutableList<Pair<NSymbol, NSymbol>>
typealias Detector = Pair<NSymbol, NSymbol>

class CYKController(val gc: GrammarController) {
    /**
     * region public methods
     */
    fun fillCYKTable(table : CYKTable){
        table.recentlyModified = false //here we go again

        //we start with terminal rules
        if(fillForUnitExample(table)) return

        //if not unit, it fills terminal rules
        fillTerminalRules(table)

        //now the real fun is coming - filling other cells
        fillNonTerminalRules(table)
    }

    fun findDetectors(table : CYKTable, y : Int, x : Int) : Detectors {
        val detectors : Detectors = mutableListOf()

        for(k in 0 until y){
            val lefts = table.cykTable[k][x]
            val rights = table.cykTable[y-k-1][x+k+1]

            for(left in lefts){
                for(right in rights){
                    detectors.add(Detector(left,right))
                }
            }
        }

        return detectors
    }

    fun isExampleParsed(table : CYKTable) = table.rootCell.contains(gc.grammar.starSymbol)

    fun wasTableUpdated(table : CYKTable) = table.recentlyModified
    //endregion

    /**
     * region private, helper methods
     */
    //here be dragons
    fun fillForUnitExample(table : CYKTable) : Boolean{
        if(table.unitExample
            && gc.tRulesWith(gc.grammar.starSymbol, table.example.parsedSequence[0]).isNotEmpty()) {     //if we have unit example
            table.rootCell.add(gc.grammar.starSymbol)
            return true
        }
        return false
    }

    fun fillTerminalRules(table : CYKTable){
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
    }

    fun fillNonTerminalRules(table : CYKTable){
        for(i in 1..table.lastIndex){
            for (j in 0..table.lastIndex-i){    //i and j iterates through cells
                table.cykTable[i][j].addAll(getEfectors(findDetectors(table, i, j)))
            }
        }
    }

    fun getEfectors(detectors: Detectors) : Set<NSymbol> {
        return detectors
            .flatMap { gc.nRulesWith(first = it.first, second = it.second) }
            .map { it.left }
            .toSet()
    }
    //endregion
}