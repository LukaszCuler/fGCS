package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.utils.Consts.Companion.END_STRING_SYMBOL

class ParseTreeController(val gc: GrammarController, val cykController: CYKController) {
    /**
     * region public methods
     */
    fun getMultiParseTreeFromCYK(cykTable : CYKTable) : MultiParseTreeNode {
        if(!cykController.isExampleParsed(cykTable)) return MultiParseTreeNode(END_STRING_SYMBOL)   //nothing to do here
        return getTreeForSymbolAndPosition(gc.grammar.starSymbol, cykTable.lastIndex, 0, cykTable)
    }

    fun processRootToNodesAll(treeToProcess: MultiParseTreeNode,
                           processFunc : ( /*"parent" node*/MultiParseTreeNode) -> Unit){
        processFunc(treeToProcess)
        if(treeToProcess.isLeaf) return
        treeToProcess
            .subtrees
            .flatMap { it.subTreePair.toList() }
            .forEach { processRootToNodesAll(it, processFunc)}
    }

    fun processSingleTreeFromRoot(treeToProcess: MultiParseTreeNode,
                              processFunc : ( /*"parent" node*/MultiParseTreeNode) -> /*returns selected child*/MultiParseTreeNode.SubTreePair?){
        val selectedSubTree = processFunc(treeToProcess)
        if(treeToProcess.isLeaf || selectedSubTree == null) return
        processSingleTreeFromRoot(selectedSubTree.subTreePair.first, processFunc)
        processSingleTreeFromRoot(selectedSubTree.subTreePair.second, processFunc)
    }

    fun processNodesToRoot(treeToProcess: MultiParseTreeNode,
                           processFunc : ( /*"parent" node*/MultiParseTreeNode) -> Unit){
        if(!treeToProcess.isLeaf){
            treeToProcess
                .subtrees
                .flatMap { it.subTreePair.toList() }
                .forEach { processNodesToRoot(it, processFunc)}
        }
        processFunc(treeToProcess)
    }
    //endregion

    /**
     * region private methods
     */
    private fun getTreeForSymbolAndPosition(symbol : NSymbol,
                                    y : Int, x : Int,
                                    cykTable : CYKTable) : MultiParseTreeNode {
        //creating current node
        val node = MultiParseTreeNode(symbol)

        //children time!
        cykController.getDetectorsForLeft(cykTable, symbol, cykController.findDetectors(cykTable, y, x))
            .forEach { node.addSubTree(Pair(
                getTreeForSymbolAndPosition(it.first.symbol, it.first.y, it.first.x, cykTable),
                getTreeForSymbolAndPosition(it.second.symbol, it.second.y, it.second.x, cykTable)))
            }
        return node
    }
}