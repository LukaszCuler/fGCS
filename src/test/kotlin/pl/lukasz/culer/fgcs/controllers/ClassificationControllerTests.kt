package pl.lukasz.culer.fgcs.controllers

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.tnorms.TNormT2
import pl.lukasz.culer.settings.Settings

@RunWith(MockitoJUnitRunner::class)
class ClassificationControllerTests {
    private lateinit var settings : Settings
    private lateinit var gc : GrammarController
    private lateinit var classificationController: ClassificationController

    @Before
    fun setUp(){
        //preparing data
        val grammar = Grammar()
        grammar.starSymbol = NSymbol('$', true)
        val aSymbol = NSymbol('A', false)
        val bSymbol = NSymbol('B', false)

        grammar.nSymbols.add(grammar.starSymbol)
        grammar.nSymbols.add(aSymbol)
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(aSymbol, aSymbol), F(1.0)))
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(grammar.starSymbol, aSymbol), F(1.0)))
        grammar.nRules.add(NRule(grammar.starSymbol, NRuleRHS(grammar.starSymbol, grammar.starSymbol), F(0.6)))
        gc = GrammarController(grammar)

        settings = Settings()

        classificationController = ClassificationController(gc, settings)
    }
}