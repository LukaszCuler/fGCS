package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode

class ParseTreeController(val gc: GrammarController, val cykController: CYKController) {
    /**
     * region public methods
     */
    fun getMultiParseTreeFromCYK(cykTable : CYKTable) : MultiParseTreeNode {
        return getTreeForSymbolAndPosition(gc.grammar.starSymbol, cykTable.lastIndex, 0, cykTable)
    }

    //endregion

    /**
     * region private methods
     */
    fun getTreeForSymbolAndPosition(symbol : NSymbol,
                                    y : Int, x : Int,
                                    cykTable : CYKTable) : MultiParseTreeNode {
        //creating current node
        val node = MultiParseTreeNode(symbol)

        //children time!
        node.subtrees.addAll(
            cykController.getDetectorsForLeft(symbol, cykController.findDetectors(cykTable, y, x))
                .map { MultiParseTreeNode.SubTrees(Pair(
                    getTreeForSymbolAndPosition(it.first.symbol, it.first.y, it.first.x, cykTable),
                    getTreeForSymbolAndPosition(it.second.symbol, it.second.y, it.second.x, cykTable))
                ) }
                .toMutableList())

        return node
    }
}