package pl.lukasz.culer.fuzzy.processors.heatmap.base

import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings

typealias SymbolDerivativeMembership = IntervalFuzzyNumber
typealias SymbolDerivativeRelevance = IntervalFuzzyNumber
typealias SymbolDerivativeData = Pair<SymbolDerivativeMembership, SymbolDerivativeRelevance>

interface HeatmapProcessor {
    fun assignDerivationMembershipToVariants(grammarController: GrammarController,
                                             inhValue : IntervalFuzzyNumber,
                                             relValue : IntervalFuzzyNumber,
                                             parseTreeNode: MultiParseTreeNode,
                                             settings: Settings)
    fun assignValueToSymbol(symbolValues : List<SymbolDerivativeData>) : SymbolDerivativeMembership    //first in pair - derivMembership, second - relevance
    fun mainTreeDistinguishable() : Boolean //can we display main tree?
    fun getMainTree(parseTreeNode: MultiParseTreeNode, grammarController: GrammarController) : MultiParseTreeNode.SubTreePair?
}