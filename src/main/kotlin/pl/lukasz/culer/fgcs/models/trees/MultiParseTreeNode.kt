package pl.lukasz.culer.fgcs.models.trees

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts

open class MultiParseTreeNode(val node : NSymbol = Consts.END_STRING_SYMBOL, val subtrees : MutableList<SubTreePair> = mutableListOf()) {
    //params to fill
    @Deprecated("should be taken from subtrees")
    var mainChild : SubTreePair? = null
    @Deprecated("should be taken from subtrees")
    var mainMembership : IntervalFuzzyNumber = F(0.0)

    //shortcuts
    val isLeaf : Boolean get() = subtrees.isEmpty()
    val isDeadEnd : Boolean get() = node == Consts.END_STRING_SYMBOL

    data class SubTreePair(val subTreePair : Pair<MultiParseTreeNode, MultiParseTreeNode> = Pair(MultiParseTreeNode(), MultiParseTreeNode()),
                           var classificationMembership : IntervalFuzzyNumber = F(0.0),
                           var relevance : IntervalFuzzyNumber = F(0.0),
                           var derivationMembership : IntervalFuzzyNumber = F(0.0),
                           var derivationRelevance : IntervalFuzzyNumber = F(0.0))
}