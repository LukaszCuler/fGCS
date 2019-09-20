package pl.lukasz.culer.fgcs.models.trees

import pl.lukasz.culer.fgcs.models.symbols.NSymbol

class SingleParseTreeNode(node : NSymbol, children: TreeNodeChildren? = null)
    : MultiParseTreeNode(node, if(children==null) mutableListOf() else mutableListOf(children)) {

    //shortcut
    val children : TreeNodeChildren get() = subtrees.single()
}