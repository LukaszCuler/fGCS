import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.data.AbbadingoLoader
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.ui.LineLauncher

@RunWith(MockitoJUnitRunner::class)
class GrammarControllerTests {

    @Test
    fun createGrammarFromDataTest(){
        val dataSet = listOf(
            TestExample("aabb"),
            TestExample("ccccb"),
            TestExample("acacac"))

        val gc = GrammarController(dataSet)

        Assert.assertEquals(3, gc.grammar.tSymbols.size) //good number of terminals
        Assert.assertEquals(4, gc.grammar.nSymbols.size)  //good number of non-terminals (start+each for terminal)
        Assert.assertNotNull(gc.grammar.starSymbol)             //we have start symbol
        Assert.assertTrue(gc.grammar.nSymbols.contains(gc.grammar.starSymbol))        //and it's present in non terminals
    }
}