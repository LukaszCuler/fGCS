package pl.lukasz.culer.fgcs.controllers

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.controllers.CYKController.DetectorElement
import pl.lukasz.culer.fgcs.models.CYKTable
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
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
        Assert.assertTrue(detectors.contains(Detector(DetectorElement(nC,0,0), DetectorElement(nC,1,1))))
        Assert.assertTrue(detectors.contains(Detector(DetectorElement(nC,0,0), DetectorElement(nE,1,1))))
        Assert.assertTrue(detectors.contains(Detector(DetectorElement(nD,1,0), DetectorElement(nC,0,2))))
        Assert.assertTrue(detectors.contains(Detector(DetectorElement(nE,1,0), DetectorElement(nC,0,2))))
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
        Assert.assertTrue(table.rootCell.contains(gc.grammar.starSymbol))
    }

    @Test
    fun fillTerminalRulesTest(){
        //preparing data
        val gc = createGrammarForExample()
        val cykController = CYKController(gc)

        val table = CYKTable(example)

        //execution
        cykController.fillTerminalRules(table)

        //verification
        val nA = gc.findNSymbolByChar('A')!!
        val nB = gc.findNSymbolByChar('B')!!
        val nC = gc.findNSymbolByChar('C')!!

        Assert.assertEquals(nA,table.cykTable[0][0].single())
        Assert.assertEquals(nB,table.cykTable[0][1].single())
        Assert.assertEquals(nC,table.cykTable[0][2].single())
        Assert.assertEquals(nA,table.cykTable[0][3].single())
    }

    @Test
    fun fillNonTerminalRulesTest(){
        //preparing data
        val gc = createGrammarForExample()
        val cykController = CYKController(gc)

        val table = CYKTable(example)

        //execution
        cykController.fillTerminalRules(table)      //needed ;_;
        cykController.fillNonTerminalRules(table)

        //verification
        val nA = gc.findNSymbolByChar('A')!!
        val nB = gc.findNSymbolByChar('B')!!
        val nC = gc.findNSymbolByChar('C')!!

        Assert.assertEquals(nA, table.cykTable[1][0].single())
        Assert.assertEquals(nB, table.cykTable[2][0].single())
        Assert.assertTrue(table.cykTable[1][1].isEmpty())
        Assert.assertTrue(table.cykTable[1][2].isEmpty())
        Assert.assertTrue(table.cykTable[2][1].isEmpty())
        Assert.assertEquals(gc.grammar.starSymbol, table.cykTable[3][0].single())
    }

    @Test
    fun getEffectorsTest(){
        //preparing data
        val gc = createGrammarForExample()
        val cykController = CYKController(gc)

        val table = CYKTable(example)
        cykController.fillTerminalRules(table)

        //execution
        val effectors = cykController.getEfectors(table, cykController.findDetectors(table, 1, 0))

        //verification
        val nA = gc.findNSymbolByChar('A')!!
        Assert.assertEquals(nA, effectors.single())
    }

    fun getDetectorsForLeftTest(){
        //preparing data
        val gc = createGrammarForExample()
        val cykController = CYKController(gc)
        val table = CYKTable(example)

        //verification
        val nA = gc.findNSymbolByChar('A')!!
        val nB = gc.findNSymbolByChar('B')!!
        val nC = gc.findNSymbolByChar('C')!!

        //execution
        val detectorsForLeft = cykController.getDetectorsForLeft(table,
            nA, mutableListOf(
                Detector(DetectorElement(nA, x = 0, y = 0), DetectorElement(nA, x = 0, y = 0)),
                Detector(DetectorElement(nA, x = 0, y = 0), DetectorElement(nB, x = 0, y = 0)),
                Detector(DetectorElement(nB, x = 0, y = 0), DetectorElement(nC, x = 0, y = 0))
            )
        )    //x and y doesn't matter

        //verification
        Assert.assertEquals(1, detectorsForLeft.size)
        Assert.assertEquals(nA, detectorsForLeft.single().first.symbol)
        Assert.assertEquals(nB, detectorsForLeft.single().second.symbol)
    }

    @Test
    fun fillCYKTableAndIsExampleParsed(){
        //stage1 - preparing data
        val gc = createGrammarForExample()
        val cykController = CYKController(gc)

        val tableParsed = CYKTable(example)

        //stage1 - execution
        cykController.fillCYKTable(tableParsed)

        //stage1 - verification
        Assert.assertTrue(cykController.isExampleParsed(tableParsed))

        //stage2 - preparing data
        gc.removeNRule(gc.nRulesWith(gc.grammar.starSymbol, gc.findNSymbolByChar('B'), gc.findNSymbolByChar('A')).single())
        val tableNotParsed = CYKTable(example)

        //stage1 - execution
        cykController.fillCYKTable(tableNotParsed)

        //stage1 - verification
        Assert.assertFalse(cykController.isExampleParsed(tableNotParsed))
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

        gc.addNRule(NRule(nA, NRuleRHS(nA, nB)))
        gc.addNRule(NRule(nB, NRuleRHS(nA, nC)))
        gc.addNRule(NRule(gc.grammar.starSymbol, NRuleRHS(nB, nA)))
        return gc
    }
}