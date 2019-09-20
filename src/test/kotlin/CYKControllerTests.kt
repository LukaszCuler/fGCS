import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.Detector
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.TRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol

@RunWith(MockitoJUnitRunner::class)
class CYKControllerTests {
    val example = TestExample("abca")
    val exampleUnit = TestExample("a")

    @Test
    fun findDetectorsTest(){
        //preparing data
        val te1 = TestExample("aabb")
        val dataSet = listOf(te1)
        val gc = GrammarController(dataSet)
        val nC = NSymbol('C').also { gc.addNSymbol(it) }
        val nD = NSymbol('D').also { gc.addNSymbol(it) }
        val nE = NSymbol('E').also { gc.addNSymbol(it) }

        val table = CYKTable(te1)

        table.cykTable[0][0].add(nC)

        table.cykTable[1][0].add(nD)
        table.cykTable[1][0].add(nE)

        table.cykTable[1][1].add(nC)
        table.cykTable[1][1].add(nE)

        table.cykTable[0][2].add(nC)

        val cykController = CYKController(gc)

        //execution
        val detectors = cykController.findDetectors(table, 2, 0)

        //verification
        Assert.assertEquals(4, detectors.size)
        Assert.assertTrue(detectors.contains(Detector(nC, nC)))
        Assert.assertTrue(detectors.contains(Detector(nC, nE)))
        Assert.assertTrue(detectors.contains(Detector(nD, nC)))
        Assert.assertTrue(detectors.contains(Detector(nE, nC)))
    }

    @Test
    fun fillForUnitExampleTest(){
        //preparing data
        val gc = createGrammarForExample()
        val cykController = CYKController(gc)

        val table = CYKTable(exampleUnit)

        //execution
        cykController.fillForUnitExample(table)

        //verification
        table.rootCell.contains(gc.grammar.starSymbol)
    }

    //private methods
    private fun createGrammarForExample() : GrammarController {
        val gc = GrammarController(listOf(example, exampleUnit))

        val ta = gc.findTSymbolByChar('a')!!
        val tb = gc.findTSymbolByChar('b')!!
        val tc = gc.findTSymbolByChar('c')!!

        val nA = gc.findNSymbolByChar('A')!!
        val nB = gc.findNSymbolByChar('B')!!
        val nC = gc.findNSymbolByChar('C')!!

        gc.grammar.tRules.add(TRule(gc.grammar.starSymbol, ta))

        gc.addNRule(NRule(nA, arrayOf(nA, nB)))
        gc.addNRule(NRule(nB, arrayOf(nA, nC)))
        gc.addNRule(NRule(gc.grammar.starSymbol, arrayOf(nB, nA)))
        return gc
    }
}