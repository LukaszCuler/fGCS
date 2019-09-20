package pl.lukasz.culer.fgcs.models.trees

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.Symbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

typealias TreeNodeChildren = Pair<MultiParseTreeNode, MultiParseTreeNode>

open class MultiParseTreeNode(val node : NSymbol, val subtrees : MutableList<TreeNodeChildren> = mutableListOf()) {
    //params to fill
    var treeMembership = IntervalFuzzyNumber(0.0)

    //shortcuts
    val isLeaf : Boolean get() = subtrees.isEmpty()
}