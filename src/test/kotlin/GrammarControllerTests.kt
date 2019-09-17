import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.data.AbbadingoLoader
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.TRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol
import pl.lukasz.culer.ui.LineLauncher

@RunWith(MockitoJUnitRunner::class)
class GrammarControllerTests {

    @Test
    fun createGrammarFromDataTest(){
        //preparing data
        val dataSet = listOf(
            TestExample("aabb"),
            TestExample("ccccb"),
            TestExample("acacac"))

        //execution
        val gc = GrammarController(dataSet)

        //validation
        Assert.assertEquals(3, gc.grammar.tSymbols.size) //good number of terminals
        Assert.assertEquals(4, gc.grammar.nSymbols.size)  //good number of non-terminals (start+each for terminal)
        Assert.assertNotNull(gc.grammar.starSymbol)             //we have start symbol
        Assert.assertTrue(gc.grammar.nSymbols.contains(gc.grammar.starSymbol))        //and it's present in non terminals
        Assert.assertEquals(3, gc.grammar.tRules.size)       //rule for each terminal
        Assert.assertEquals(0, gc.grammar.nRules.size)       //but not for non-terminals
        //probably no need in checking exact rules
    }

    @Test
    fun updateSymbolReferencesTest(){
        //preparing data
        val grammar = Grammar()
        grammar.tSymbols.add(TSymbol('a'))
        grammar.starSymbol = NSymbol('$')
        grammar.nSymbols.add(grammar.starSymbol)
        grammar.tRules.add(TRule(NSymbol('$'), TSymbol('a')))
        grammar.nRules.add(NRule(NSymbol('$'), arrayOf(NSymbol('$'), NSymbol('$'))))

        //execution
        val gc = GrammarController(grammar)

        //validation
        //references should be substituted
        Assert.assertTrue(grammar.tRules.single().left === gc.grammar.nSymbols.single())
        Assert.assertTrue(grammar.tRules.single().getRight() === gc.grammar.tSymbols.single())

        Assert.assertTrue(grammar.nRules.single().left === gc.grammar.nSymbols.single())
        Assert.assertTrue(grammar.nRules.single().getRightFirst() === gc.grammar.nSymbols.single())
        Assert.assertTrue(grammar.nRules.single().getRightSecond() === gc.grammar.nSymbols.single())
    }

    @Test
    fun removeUnreachableAndUnproductiveRules(){
        //preparing data
        val ta = TSymbol('a')
        val nS = NSymbol('$')
        val nA = NSymbol('A')
        val nB = NSymbol('B')
        val nC = NSymbol('C')

        val grammar = Grammar()
        val gc = GrammarController(grammar)

        grammar.tSymbols.add(ta)
        grammar.nSymbols.add(nS)
        grammar.nSymbols.add(nA)
        grammar.nSymbols.add(nB)
        grammar.nSymbols.add(nC)
        grammar.starSymbol = nS

        grammar.tRules.add(TRule(nA,ta))
        grammar.nRules.add(NRule(nS, arrayOf(nA,nA)))
        grammar.nRules.add(NRule(nS, arrayOf(nB,nB)))       //unproductive
        grammar.nRules.add(NRule(nC, arrayOf(nA,nA)))       //unreachable

        //execution
        gc.removeUnreachableAndUnproductiveRules()

        //validation
        Assert.assertEquals(1,grammar.tRules.size) //two of three were removed
    }

    @Test
    fun removeUnusedSymbolsRules(){
        //preparing data
        val ta = TSymbol('a')
        val nS = NSymbol('$')
        val nA = NSymbol('A')
        val nB = NSymbol('B')

        val grammar = Grammar()
        val gc = GrammarController(grammar)

        grammar.tSymbols.add(ta)
        grammar.nSymbols.add(nS)
        grammar.nSymbols.add(nA)
        grammar.nSymbols.add(nB)
        grammar.starSymbol = nS

        grammar.tRules.add(TRule(nA,ta))
        grammar.nRules.add(NRule(nS, arrayOf(nA,nA)))

        //execution
        gc.removeUnusedSymbols()

        //validation
        Assert.assertEquals(2,grammar.nSymbols.size) //one symbol removed
    }
}