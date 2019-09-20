package pl.lukasz.culer.fgcs.models.trees

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

open class MultiParseTreeNode(val node : NSymbol, val subtrees : MutableList<SubTrees> = mutableListOf()) {
    //params to fill
    var mainChild : SubTrees? = null

    //shortcuts
    val isLeaf : Boolean get() = subtrees.isEmpty()

    data class SubTrees(val subtrees : Pair<MultiParseTreeNode, MultiParseTreeNode>,
                        var treeMembership : IntervalFuzzyNumber = IntervalFuzzyNumber(0.0))
}