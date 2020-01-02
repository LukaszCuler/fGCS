package pl.lukasz.culer.fuzzy.processors.heatmap

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.heatmap.base.SymbolDerivativeData
import pl.lukasz.culer.fuzzy.processors.heatmap.base.SymbolDerivativeMembership
import pl.lukasz.culer.fuzzy.processors.heatmap.base.SymbolDerivativeRelevance
import pl.lukasz.culer.fuzzy.tnorms.TNormT2
import pl.lukasz.culer.settings.Settings

@RunWith(MockitoJUnitRunner::class)
class MaxMembershipHeatmapProcessorTests {
    private lateinit var processor : MaxMembershipHeatmapProcessor
    private lateinit var settings : Settings
    private lateinit var gc : GrammarController
    private lateinit var aaPair : MultiParseTreeNode.SubTreePair
    private lateinit var saPair : MultiParseTreeNode.SubTreePair
    private lateinit var ssPair : MultiParseTreeNode.SubTreePair
    private lateinit var parseTree : MultiParseTreeNode

    private lateinit var symbolValues: List<SymbolDerivativeData>

    @Before
    fun init(){
        processor = MaxMembershipHeatmapProcessor()
        //preparing data
        val grammar = Grammar()
        grammar.starSymbol = NSymbol('$', true)
        val aSymbol = NSymbol('A', false)
        grammar.nSymbols.add(grammar.starSymbol)
        grammar.nSymbols.add(aSymbol)
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(aSymbol, aSymbol), F(1.0)))
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(grammar.starSymbol, aSymbol), F(0.5)))
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(grammar.starSymbol, grammar.starSymbol), F(0.1)))
        gc = GrammarController(grammar)

        settings = Settings()
        settings.tNorm = TNormT2.MIN

        aaPair = MultiParseTreeNode.SubTreePair(Pair(MultiParseTreeNode(aSymbol), MultiParseTreeNode(aSymbol)))
        saPair = MultiParseTreeNode.SubTreePair(Pair(MultiParseTreeNode(grammar.starSymbol), MultiParseTreeNode(aSymbol)))
        ssPair = MultiParseTreeNode.SubTreePair(Pair(MultiParseTreeNode(grammar.starSymbol), MultiParseTreeNode(grammar.starSymbol)))

        parseTree = MultiParseTreeNode(grammar.starSymbol, mutableListOf(aaPair, saPair, ssPair))

        symbolValues = listOf(
            SymbolDerivativeData(SymbolDerivativeMembership(0.8), SymbolDerivativeRelevance(0.2)),
            SymbolDerivativeData(SymbolDerivativeMembership(0.3), SymbolDerivativeRelevance(0.7)),
            SymbolDerivativeData(SymbolDerivativeMembership(0.7), SymbolDerivativeRelevance(0.7))
        )
    }

    @Test
    fun assignDerivationMembershipToVariantsTest(){
        //work
        processor.assignDerivationMembershipToVariants(gc, IntervalFuzzyNumber(1.0), IntervalFuzzyNumber(1.0), parseTree, settings)

        //verification
        Assert.assertTrue(aaPair.derivationMembership.equals(1.0))
        Assert.assertTrue(saPair.derivationMembership.equals(0.0))
        Assert.assertTrue(ssPair.derivationMembership.equals(0.0))
        Assert.assertTrue(aaPair.derivationRelevance.equals(1.0))
        Assert.assertTrue(saPair.derivationRelevance.equals(0.0))
        Assert.assertTrue(ssPair.derivationRelevance.equals(0.0))
    }

    @Test
    fun assignValueToSymbolTest(){
        Assert.assertTrue(processor.assignValueToSymbol(symbolValues).equals(0.8))
    }
}