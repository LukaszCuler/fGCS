package pl.lukasz.culer.fgcs.models.trees

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts

open class MultiParseTreeNode(val node : NSymbol = Consts.END_STRING_SYMBOL, val subtrees : MutableList<SubTreePair> = mutableListOf()) {
    //params to fill
    var mainChild : SubTreePair? = null
    var mainMembership : IntervalFuzzyNumber = IntervalFuzzyNumber(0.0)

    //shortcuts
    val isLeaf : Boolean get() = subtrees.isEmpty()
    val isDeadEnd : Boolean get() = node == Consts.END_STRING_SYMBOL

    data class SubTreePair(val subTreePair : Pair<MultiParseTreeNode, MultiParseTreeNode>,
                           var classificationMembership : IntervalFuzzyNumber = IntervalFuzzyNumber(0.0),
                           var derivationMembership : IntervalFuzzyNumber = IntervalFuzzyNumber(0.0),
                           var relevance : IntervalFuzzyNumber = IntervalFuzzyNumber(0.0))
}