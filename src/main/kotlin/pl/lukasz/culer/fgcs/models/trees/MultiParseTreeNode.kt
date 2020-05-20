package pl.lukasz.culer.fgcs.models.trees

import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts

open class MultiParseTreeNode(var node : NSymbol = Consts.END_STRING_SYMBOL, val subtrees : MutableList<SubTreePair> = mutableListOf()) {
    var classificationMembership : IntervalFuzzyNumber = F(0.0)

    //shortcuts
    val isLeaf : Boolean get() = subtrees.isEmpty()
    val isDeadEnd : Boolean get() = node == Consts.END_STRING_SYMBOL

    override fun toString() = "$node"

    fun addSubTree(subTreePair : Pair<MultiParseTreeNode, MultiParseTreeNode> = Pair(MultiParseTreeNode(), MultiParseTreeNode()),
                   classificationMembership : IntervalFuzzyNumber = F(0.0),
                   relevance : IntervalFuzzyNumber = F(0.0),
                   derivationMembership : IntervalFuzzyNumber = F(0.0),
                    derivationRelevance : IntervalFuzzyNumber = F(0.0)) : SubTreePair {
        return SubTreePair(subTreePair, classificationMembership, relevance, derivationMembership, derivationRelevance)
            .apply {
                subtrees.add(this)
            }
    }

    inner class SubTreePair(var subTreePair : Pair<MultiParseTreeNode, MultiParseTreeNode>,
                            var classificationMembership : IntervalFuzzyNumber,
                            var relevance : IntervalFuzzyNumber,
                            var derivationMembership : IntervalFuzzyNumber,
                            var derivationRelevance : IntervalFuzzyNumber) {
        override fun toString() = "$node->${subTreePair.first}${subTreePair.second}"
    }
}