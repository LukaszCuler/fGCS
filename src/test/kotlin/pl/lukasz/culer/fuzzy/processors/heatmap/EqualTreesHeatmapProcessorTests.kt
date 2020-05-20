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
import pl.lukasz.culer.fuzzy.processors.heatmap.base.SymbolDerivativeData
import pl.lukasz.culer.fuzzy.processors.heatmap.base.SymbolDerivativeMembership
import pl.lukasz.culer.fuzzy.processors.heatmap.base.SymbolDerivativeRelevance
import pl.lukasz.culer.fuzzy.tnorms.TNormT2
import pl.lukasz.culer.settings.Settings

@RunWith(MockitoJUnitRunner::class)
class EqualTreesHeatmapProcessorTests {
    private lateinit var processor : EqualTreesHeatmapProcessor
    private lateinit var settings : Settings
    private lateinit var gc : GrammarController
    private lateinit var aaPair : MultiParseTreeNode.SubTreePair
    private lateinit var saPair : MultiParseTreeNode.SubTreePair
    private lateinit var ssPair : MultiParseTreeNode.SubTreePair
    private lateinit var parseTree : MultiParseTreeNode

    private lateinit var symbolValues: List<SymbolDerivativeData>

    @Before
    fun init(){
        processor = EqualTreesHeatmapProcessor()
        //preparing data
        settings = Settings()
        settings.tOperatorRev = TNormT2.MIN

        val grammar = Grammar()
        grammar.starSymbol = NSymbol('$', true)
        val aSymbol = NSymbol('A', false)
        grammar.nSymbols.add(grammar.starSymbol)
        grammar.nSymbols.add(aSymbol)
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(aSymbol, aSymbol), F(1.0)))
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(grammar.starSymbol, aSymbol), F(1.0)))
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(grammar.starSymbol, grammar.starSymbol), F(0.6)))
        gc = GrammarController(settings, grammar)

        parseTree = MultiParseTreeNode(grammar.starSymbol)
        aaPair = parseTree.addSubTree(Pair(MultiParseTreeNode(aSymbol), MultiParseTreeNode(aSymbol)), relevance = F(1.0))
        saPair = parseTree.addSubTree(Pair(MultiParseTreeNode(grammar.starSymbol), MultiParseTreeNode(aSymbol)), relevance = F(0.5))
        ssPair = parseTree.addSubTree(Pair(MultiParseTreeNode(grammar.starSymbol), MultiParseTreeNode(grammar.starSymbol)), relevance = F(0.5))

        symbolValues = listOf(
            SymbolDerivativeData(SymbolDerivativeMembership(1.0), SymbolDerivativeRelevance(1.0)),
            SymbolDerivativeData(SymbolDerivativeMembership(1.0), SymbolDerivativeRelevance(0.5)),
            SymbolDerivativeData(SymbolDerivativeMembership(0.7), SymbolDerivativeRelevance(0.5))
        )
    }

    @Test
    fun assignDerivationMembershipToVariantsTest(){
        //work
        processor.assignDerivationMembershipToVariants(gc, F(1.0), F(1.0), parseTree, settings)

        //verification
        Assert.assertTrue(aaPair.derivationMembership.equals(1.0))
        Assert.assertTrue(saPair.derivationMembership.equals(1.0))
        Assert.assertTrue(ssPair.derivationMembership.equals(0.6))
        Assert.assertTrue(aaPair.derivationRelevance.equals(1.0))
        Assert.assertTrue(saPair.derivationRelevance.equals(1.0))
        Assert.assertTrue(ssPair.derivationRelevance.equals(1.0))
    }

    @Test
    fun assignValueToSymbolTest(){
        Assert.assertTrue(processor.assignValueToSymbol(symbolValues).equals(0.9))
    }
}