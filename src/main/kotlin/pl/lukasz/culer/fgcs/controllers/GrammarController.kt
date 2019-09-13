package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.TRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.DEFAULT_START_SYMBOL

class GrammarController {
    /**region params **/
    lateinit var grammar : Grammar
    var learningData : List<TestExample> = mutableListOf()
    var testData : List<TestExample>? = null
    /** endregion **/

    /**region constructors **/
    //creating grammar from data
    constructor(learningData : List<TestExample>){
        this.learningData = learningData
        createGrammarFromData()
    }

    //simply loading grammar
    constructor(grammar: Grammar){
        this.grammar = grammar
    }
    /** endregion **/

    /**region public methods **/

    /** endregion **/

    /** region private methods **/
    //constructing grammar
    private fun createGrammarFromData(){
        //obtaining list of terminal chars
        grammar.tSymbols.addAll(
            learningData
                .flatMap { it.sequence.asIterable() }
                .toHashSet()
                .map { TSymbol(it) }
                .toSet())

        //obtaining list of non-terminal counterparts chars
        grammar.nSymbols.addAll(grammar.tSymbols.map { NSymbol(it.symbol.toUpperCase()) })

        //creating start symbol
        grammar.starSymbol = NSymbol(DEFAULT_START_SYMBOL, true)
        grammar.nSymbols.add(grammar.starSymbol)

        //"terminal covering"
        grammar.tRules.addAll(grammar.tSymbols
            .map {
                val leftSymbol = findNSymbolByChar(it.symbol.toUpperCase())
                if(leftSymbol!=null) TRule(leftSymbol, it)
                else null
            }
            .filterNotNull()
            .toSet())
    }

    private fun createTemporaryVariables(){
        grammar.nRulesMap = mutableMapOf()
        for(leftSymbol : NSymbol in grammar.nSymbols){

            val rightSide : MutableMap<NSymbol, MutableMap<NSymbol, NRule?>> = mutableMapOf()
            grammar.nRulesMap[leftSymbol] = rightSide

            for(firstRightSymbol : NSymbol in grammar.nSymbols){
                val secondSymbolSet : MutableMap<NSymbol, NRule?> = mutableMapOf()
                rightSide[firstRightSymbol] = secondSymbolSet
            }
        }
    }

    private fun fillTemporaryVariables(){
        grammar.nRules.forEach {
            //grammar.nRulesMap[it.left][it.getRightFirst()][it.getRightSecond()]
        }
    }

    //cleaning stuff
    private fun removeUnreachableAndUnproductiveRules(){

    }

    private fun removeUnusedSymbols(){

    }

    //helper symbols functions
    private fun getNRuleBySymbols() : NRule?{
        return null
    }

    private fun setStartSymbol(newStartSymbol : NSymbol) {
        grammar.starSymbol.isStartSymbol = false
        newStartSymbol.isStartSymbol = true

        grammar.starSymbol = newStartSymbol
        grammar.nSymbols.add(newStartSymbol)
    }

    private fun getNewNSymbol() : NSymbol?{
        var currentSymbol = Consts.N_GEN_START
        while(currentSymbol<= Char.MAX_VALUE){
            val findNSymbolByChar = findNSymbolByChar(currentSymbol)
            if(findNSymbolByChar == null){
                val newSymbol = NSymbol(currentSymbol)
                grammar.nSymbols.add(newSymbol)
                return newSymbol
            }
            currentSymbol++
        }
        return null     //no more chars
    }

    private fun findTSymbolByChar(symbolChar : Char) : TSymbol? {
        return grammar.tSymbols.find { it.symbol == symbolChar }
    }

    private fun findNSymbolByChar(symbolChar : Char) : NSymbol? {
        return grammar.nSymbols.find { it.symbol == symbolChar }
    }
    /** endregion **/
}