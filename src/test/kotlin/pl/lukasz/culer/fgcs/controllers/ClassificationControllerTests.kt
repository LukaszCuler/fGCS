package pl.lukasz.culer.fgcs.controllers

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
import pl.lukasz.culer.fgcs.models.rules.TRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.memberships.SubtreeMembershipT2
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessorFactory
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessorFactory
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts

@RunWith(MockitoJUnitRunner::class)
class ClassificationControllerTests {
    private lateinit var settings : Settings
    private lateinit var gc : GrammarController
    private lateinit var classificationController: ClassificationController
    private lateinit var multiParseTreeFromCYK : MultiParseTreeNode

    @Before
    fun setUp(){
        //preparing data
        val grammar = Grammar()
        grammar.starSymbol = NSymbol('$', true)
        val aSymbol = NSymbol('A', false)
        val bSymbol = NSymbol('B', false)
        val atSymbol = TSymbol('a', false)
        val btSymbol = TSymbol('b', false)

        grammar.nSymbols.add(grammar.starSymbol)
        grammar.nSymbols.add(aSymbol)
        grammar.nSymbols.add(bSymbol)
        grammar.tSymbols.add(atSymbol)
        grammar.tSymbols.add(btSymbol)
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(aSymbol, bSymbol), F(1.0)))
        grammar.nRules.add(NRule(aSymbol, NRuleRHS(aSymbol, aSymbol), F(1.0)))
        grammar.nRules.add(NRule(bSymbol, NRuleRHS(bSymbol, bSymbol), F(0.5)))
        grammar.tRules.add(TRule(aSymbol, atSymbol))
        grammar.tRules.add(TRule(bSymbol, btSymbol))
        gc = GrammarController(grammar)

        settings = Settings()
        settings.tOperatorReg = SubtreeMembershipT2.MIN
        settings.relevanceProcessorFactory = RelevanceProcessorFactory.WTA
        settings.heatmapProcessorFactory = HeatmapProcessorFactory.MINMAX
        settings.crispClassificationThreshold = 0.4

        val cykController = CYKController(gc)
        val testExample = TestExample("aabb", F(1.0))
        testExample.parse(gc)
        val cykTable = CYKTable(testExample)
        cykController.fillCYKTable(cykTable)
        multiParseTreeFromCYK = ParseTreeController(gc, cykController).getMultiParseTreeFromCYK(cykTable)
        classificationController = ClassificationController(gc, settings)
    }

    @Test
    fun assignClassificationMembershipTest(){
        classificationController.assignClassificationMembership(multiParseTreeFromCYK)
        Assert.assertTrue(multiParseTreeFromCYK.classificationMembership.midpoint.equals(0.5))
    }

    @Test
    fun assignRelevanceTest(){
        classificationController.assignRelevance(multiParseTreeFromCYK)

        //every subtree should have assigned relevance
        checkIfTreeAndChildrenHaveFullRelevance(multiParseTreeFromCYK)
    }

    @Test
    fun assignDerivationMembershipTest(){
        val derivationMemberships = classificationController.assignDerivationMembership(multiParseTreeFromCYK)

        Assert.assertEquals(4, derivationMemberships.size)

        Assert.assertEquals(1, derivationMemberships[0].size)
        Assert.assertEquals(1, derivationMemberships[1].size)
        Assert.assertEquals(1, derivationMemberships[2].size)
        Assert.assertEquals(1, derivationMemberships[3].size)

        Assert.assertEquals(Pair(F(1.0), F(1.0)),derivationMemberships[0].first())
        Assert.assertEquals(Pair(F(1.0), F(1.0)),derivationMemberships[1].first())
        Assert.assertEquals(Pair(F(0.5), F(1.0)),derivationMemberships[2].first())
        Assert.assertEquals(Pair(F(0.5), F(1.0)),derivationMemberships[3].first())
    }

    @Test @Deprecated("mainMembership & mainChild are deprecated")
    fun getFuzzyClassificationTest(){
        classificationController.assignClassificationMembership(multiParseTreeFromCYK)
        Assert.assertEquals(F(0.5), multiParseTreeFromCYK.classificationMembership)
    }

    @Test
    fun getCrispClassificationTest(){
        classificationController.assignClassificationMembership(multiParseTreeFromCYK)
        Assert.assertTrue(classificationController.getCrispClassification(multiParseTreeFromCYK))
    }

    @Test
    fun getExampleHeatmapTest(){
        val exampleHeatmap = classificationController.getExampleHeatmap(multiParseTreeFromCYK)

        //formulas checked at
        Assert.assertEquals(4, exampleHeatmap.size)

        Assert.assertEquals(F(1.0),exampleHeatmap[0])
        Assert.assertEquals(F(1.0),exampleHeatmap[1])
        Assert.assertEquals(F(0.5),exampleHeatmap[2])
        Assert.assertEquals(F(0.5),exampleHeatmap[3])
    }

    //private functions
    private fun checkIfTreeAndChildrenHaveFullRelevance(parseTree : MultiParseTreeNode) {
        if(parseTree.isLeaf) return
        for(childVariant in parseTree.subtrees){
            Assert.assertEquals(Consts.FULL_RELEVANCE, childVariant.relevance)
            checkIfTreeAndChildrenHaveFullRelevance(childVariant.subTreePair.first)
            checkIfTreeAndChildrenHaveFullRelevance(childVariant.subTreePair.second)
        }
    }
}