package pl.lukasz.culer.fgcs.measures

import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.measures.grammar.EntropyGrammarPerfectionMeasure
import pl.lukasz.culer.fgcs.measures.grammar.EntropyGrammarPerfectionMeasure.Companion.FULLY_FUZZIFIED
import pl.lukasz.culer.fgcs.measures.grammar.EntropyGrammarPerfectionMeasure.Companion.MIDDLE
import pl.lukasz.culer.fgcs.measures.grammar.EntropyGrammarPerfectionMeasure.Companion.NOT_FUZZIFIED
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.settings.Settings

@RunWith(MockitoJUnitRunner::class)
class EntropyGrammarPerfectionMeasureTests {
    companion object {
        const val ENT_EPS = 0.01
    }

    @Test
    fun getDoubleMeasureTest(){
        val measure = EntropyGrammarPerfectionMeasure()

        val grammarControllerFirst = GrammarController(Settings(), mutableListOf())
        grammarControllerFirst.addNRule(NRule(grammarControllerFirst.getNewNSymbol()!!,
            NRuleRHS(grammarControllerFirst.getNewNSymbol()!!,
                grammarControllerFirst.getNewNSymbol()!!),
        F(1.0)))
        grammarControllerFirst.addNRule(NRule(grammarControllerFirst.getNewNSymbol()!!,
            NRuleRHS(grammarControllerFirst.getNewNSymbol()!!,
                grammarControllerFirst.getNewNSymbol()!!),
            F(1.0)))

        Assert.assertEquals(NOT_FUZZIFIED, measure.getDoubleMeasure(grammarControllerFirst.grammar, mutableListOf()), ENT_EPS)

        val grammarControllerSecond = GrammarController(Settings(), mutableListOf())
        grammarControllerSecond.addNRule(NRule(grammarControllerSecond.getNewNSymbol()!!,
            NRuleRHS(grammarControllerSecond.getNewNSymbol()!!,
                grammarControllerSecond.getNewNSymbol()!!),
            F(0.5)))
        grammarControllerSecond.addNRule(NRule(grammarControllerSecond.getNewNSymbol()!!,
            NRuleRHS(grammarControllerSecond.getNewNSymbol()!!,
                grammarControllerSecond.getNewNSymbol()!!),
            F(0.5)))

        Assert.assertEquals(FULLY_FUZZIFIED, measure.getDoubleMeasure(grammarControllerSecond.grammar, mutableListOf()), ENT_EPS)

        val grammarControllerThird = GrammarController(Settings(), mutableListOf())
        grammarControllerThird.addNRule(NRule(grammarControllerThird.getNewNSymbol()!!,
            NRuleRHS(grammarControllerThird.getNewNSymbol()!!,
                grammarControllerThird.getNewNSymbol()!!),
            F(0.5)))
        grammarControllerThird.addNRule(NRule(grammarControllerThird.getNewNSymbol()!!,
            NRuleRHS(grammarControllerThird.getNewNSymbol()!!,
                grammarControllerThird.getNewNSymbol()!!),
            F(1.0)))

        Assert.assertEquals(0.33, measure.getDoubleMeasure(grammarControllerThird.grammar, mutableListOf()), ENT_EPS)
    }

    @Test
    fun isGrammarPerfectTest(){
        val measure = EntropyGrammarPerfectionMeasure()
        assertTrue(measure.isGrammarPerfect(NOT_FUZZIFIED))
        assertFalse(measure.isGrammarPerfect(FULLY_FUZZIFIED))
        assertFalse(measure.isGrammarPerfect(MIDDLE))
    }
}