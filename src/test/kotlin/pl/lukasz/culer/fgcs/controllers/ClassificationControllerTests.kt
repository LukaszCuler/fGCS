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
import pl.lukasz.culer.fuzzy.tnorms.TNormT2
import pl.lukasz.culer.settings.Settings

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
        settings.subtreeMembership = SubtreeMembershipT2.MIN

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
        Assert.assertTrue(multiParseTreeFromCYK.mainMembership.equals(0.5))
    }
}