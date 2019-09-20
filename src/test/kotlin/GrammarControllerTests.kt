import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
import pl.lukasz.culer.fgcs.models.rules.TRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol
import pl.lukasz.culer.utils.Consts

@RunWith(MockitoJUnitRunner::class)
class GrammarControllerTests {

    @Test
    fun createGrammarFromDataTest(){
        //preparing data
        val te1 = TestExample("aabb")
        val te2 = TestExample("ccccb")
        val te3 = TestExample("acacac")
        val dataSet = listOf(te1, te2, te3)

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

        //examples validation
        val ta = gc.findTSymbolByChar('a')
        val tb = gc.findTSymbolByChar('b')
        val tc = gc.findTSymbolByChar('c')

        Assert.assertEquals(4, te1.parsedSequence.size)
        Assert.assertArrayEquals(arrayOf(ta, ta, tb, tb), te1.parsedSequence)

        Assert.assertEquals(5, te2.parsedSequence.size)
        Assert.assertArrayEquals(arrayOf(tc, tc, tc, tc, tb), te2.parsedSequence)

        Assert.assertEquals(6, te3.parsedSequence.size)
        Assert.assertArrayEquals(arrayOf(ta, tc, ta, tc, ta, tc), te3.parsedSequence)
    }

    @Test
    fun updateSymbolReferencesTest(){
        //preparing data
        val grammar = Grammar()
        grammar.tSymbols.add(TSymbol('a'))
        grammar.starSymbol = NSymbol('$', true)
        grammar.nSymbols.add(grammar.starSymbol)
        grammar.tRules.add(TRule(NSymbol('$', true), TSymbol('a')))
        grammar.nRules.add(NRule(NSymbol('$', true), NRuleRHS(NSymbol('$'), NSymbol('$'))))

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
        val nS = NSymbol('$', true)
        val nA = NSymbol('A')
        val nB = NSymbol('B')
        val nC = NSymbol('C')

        val grammar = Grammar()

        grammar.tSymbols.add(ta)
        grammar.nSymbols.add(nS)
        grammar.nSymbols.add(nA)
        grammar.nSymbols.add(nB)
        grammar.nSymbols.add(nC)
        grammar.starSymbol = nS

        grammar.tRules.add(TRule(nA,ta))
        grammar.nRules.add(NRule(nS, NRuleRHS(nA,nA)))
        grammar.nRules.add(NRule(nS, NRuleRHS(nB,nB)))       //unproductive
        grammar.nRules.add(NRule(nC, NRuleRHS(nA,nA)))       //unreachable

        val gc = GrammarController(grammar)

        //execution
        gc.removeUnreachableAndUnproductiveRules()

        //validation
        Assert.assertEquals(1,grammar.tRules.size) //two of three were removed
    }

    @Test
    fun removeUnusedSymbolsRules(){
        //preparing data
        val grammar = getSimpleGrammar()
        grammar.nSymbols.add(NSymbol('B'))
        val gc = GrammarController(grammar)

        //execution
        gc.removeUnusedSymbols()

        //validation
        Assert.assertEquals(2,grammar.nSymbols.size) //one symbol removed
    }

    @Test
    fun addNRuleTest(){     //simple for now, probably will be extended
        //preparing data
        val grammar = getSimpleGrammar()
        val gc = GrammarController(grammar)

        val newRule = NRule(grammar.starSymbol, NRuleRHS(grammar.starSymbol, grammar.starSymbol))

        val nRulesNumBefore = grammar.nRules.size
        val containsBefore = gc.containsNRule(newRule)

        //execution
        gc.addNRule(newRule)

        //verification
        Assert.assertTrue(nRulesNumBefore<grammar.nRules.size)
        Assert.assertTrue(!containsBefore && gc.containsNRule(newRule))
    }

    @Test
    fun removeNRuleTest(){     //simple for now, probably will be extended
        //preparing data
        val grammar = getSimpleGrammar()
        val gc = GrammarController(grammar)

        val ruleToRemove = grammar.nRules.single() //should be S->AA

        val nRulesNumBefore = grammar.nRules.size
        val containsBefore = gc.containsNRule(ruleToRemove)

        //execution
        gc.removeNRule(ruleToRemove)

        //verification
        Assert.assertTrue(nRulesNumBefore>grammar.nRules.size)
        Assert.assertTrue(containsBefore && !gc.containsNRule(ruleToRemove))
    }

    @Test
    fun addNSymbolTest(){     //simple for now, probably will be extended
        //preparing data
        val grammar = getSimpleGrammar()
        val gc = GrammarController(grammar)

        val newSymbol = NSymbol('N')

        val nSymbolsNumBefore = grammar.nSymbols.size
        val containsBefore = grammar.nSymbols.contains(newSymbol)

        //execution
        gc.addNSymbol(newSymbol)

        //verification
        Assert.assertTrue(nSymbolsNumBefore<grammar.nSymbols.size)
        Assert.assertTrue(!containsBefore && grammar.nSymbols.contains(newSymbol))
    }

    @Test
    fun removeNSymbolTest(){     //simple for now, probably will be extended
        //preparing data
        val grammar = getSimpleGrammar()
        val gc = GrammarController(grammar)

        val symbolToRemove = gc.findNSymbolByChar('A')

        val nSymbolsNumBefore = grammar.nSymbols.size
        val containsBefore = grammar.nSymbols.contains(symbolToRemove)

        //execution
        if(symbolToRemove!=null) gc.removeNSymbol(symbolToRemove)

        //verification
        Assert.assertTrue(nSymbolsNumBefore>grammar.nSymbols.size)
        Assert.assertTrue(containsBefore && !grammar.nSymbols.contains(symbolToRemove))
    }

    @Test
    fun rulesWithTest(){
        //preparing data
        val ta = TSymbol('a')
        val nS = NSymbol('$', true)
        val nA = NSymbol('A')
        val nB = NSymbol('B')

        val grammar = Grammar()

        grammar.tSymbols.add(ta)
        grammar.nSymbols.add(nS)
        grammar.nSymbols.add(nA)
        grammar.nSymbols.add(nB)
        grammar.starSymbol = nS

        grammar.tRules.add(TRule(nA,ta))
        grammar.nRules.add(NRule(nS, NRuleRHS(nA,nB)))
        grammar.nRules.add(NRule(nS, NRuleRHS(nA,nS)))
        grammar.nRules.add(NRule(nA, NRuleRHS(nS,nB)))

        val gc = GrammarController(grammar)

        //validation
        Assert.assertEquals(2, gc.nRulesWith(left = nS).size)
        Assert.assertEquals(2, gc.nRulesWith(first = nA).size)
        Assert.assertEquals(2, gc.nRulesWith(second = nB).size)
        Assert.assertEquals(2, gc.nRulesWith(left = nS, first = nA).size)
    }

    @Test
    fun setStartSymbolTest(){
        //preparing data
        val grammar = getSimpleGrammar()
        val gc = GrammarController(grammar)

        val newStartSymbol = gc.findNSymbolByChar('A')
        val oldStartSymbol = gc.findNSymbolByChar(Consts.DEFAULT_START_SYMBOL)

        //pre verification
        Assert.assertFalse(newStartSymbol==null||newStartSymbol.isStartSymbol)
        Assert.assertTrue(oldStartSymbol!=null&&oldStartSymbol.isStartSymbol)
        Assert.assertEquals(oldStartSymbol, grammar.starSymbol)

        //executiion
        if(newStartSymbol!=null) gc.setStartSymbol(newStartSymbol)

        //verification
        Assert.assertFalse(oldStartSymbol==null||oldStartSymbol.isStartSymbol)
        Assert.assertTrue(newStartSymbol!=null&&newStartSymbol.isStartSymbol)
        Assert.assertEquals(newStartSymbol, grammar.starSymbol)
    }

    @Test
    fun getNewNSymbolTest(){
        //preparing data
        val grammarPre = getSimpleGrammar()
        val grammarModified = getSimpleGrammar()
        val gcMod = GrammarController(grammarModified)

        //execution
        val newSymbol = gcMod.getNewNSymbol()

        //verification
        Assert.assertNotNull(newSymbol)
        Assert.assertFalse(grammarPre.nSymbols.contains(newSymbol))
        Assert.assertTrue(grammarModified.nSymbols.contains(newSymbol))
        Assert.assertEquals(grammarPre.nSymbols.size+1, grammarModified.nSymbols.size)
    }

    @Test
    fun findTSymbolByCharTest(){
        //preparing data
        val grammar = getSimpleGrammar()
        val gc = GrammarController(grammar)

        //execution & verification
        Assert.assertNotNull(gc.findTSymbolByChar('a'))
        Assert.assertNull(gc.findTSymbolByChar('b'))
    }

    @Test
    fun findNSymbolByCharTest(){
        //preparing data
        val grammar = getSimpleGrammar()
        val gc = GrammarController(grammar)

        //execution & verification
        Assert.assertNotNull(gc.findNSymbolByChar('$'))
        Assert.assertNull(gc.findNSymbolByChar('B'))
    }

    //helper methods
    private fun getSimpleGrammar() : Grammar {
        //preparing data
        val ta = TSymbol('a')
        val nS = NSymbol('$', true)
        val nA = NSymbol('A')

        val grammar = Grammar()

        grammar.tSymbols.add(ta)
        grammar.nSymbols.add(nS)
        grammar.nSymbols.add(nA)
        grammar.starSymbol = nS

        grammar.tRules.add(TRule(nA,ta))
        grammar.nRules.add(NRule(nS, NRuleRHS(nA,nA)))
        return grammar
    }
}