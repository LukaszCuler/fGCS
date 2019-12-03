package pl.lukasz.culer.fuzzy.processors.heatmap.base

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings

interface HeatmapProcessor {
    fun assignDerivationMembershipToVariants(inhValue : IntervalFuzzyNumber,
                                             relValue : IntervalFuzzyNumber,
                                             children: List<MultiParseTreeNode.SubTreePair>,
                                             settings: Settings)
    fun assignValueToSymbol(symbolValues : List<Pair<IntervalFuzzyNumber, IntervalFuzzyNumber>>) : IntervalFuzzyNumber    //first in pair - derivMembership, second - relevance
    fun mainTreeDistinguishable() : Boolean //can we display main tree?
    fun getMainTree(children: List<MultiParseTreeNode.SubTreePair>) : MultiParseTreeNode.SubTreePair?
}