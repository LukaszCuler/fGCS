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

    //cleaning stuff
    private fun removeUnreachableAndUnproductiveRules(){
        //first we have to deal witch achievability

        val achievableRules : MutableSet<NRule> = mutableSetOf()
        //symbols first
        val achievableSymbols = mutableSetOf(grammar.starSymbol)
        var recentlyAdded = mutableSetOf(grammar.starSymbol)
        while (recentlyAdded.isNotEmpty()){
            recentlyAdded = recentlyAdded
                .flatMap { rulesWith(left = it) }
                .map {
                    achievableRules.add(it)
                    it
                }
                .flatMap { setOf(it.getRightFirst(), it.getRightSecond()) }
                .filter { !achievableSymbols.contains(it) }
                .toMutableSet()
            achievableSymbols.addAll(recentlyAdded)
        }

        //now we remove unachievable rules
        grammar.nRules
            .filter { !achievableRules.contains(it) }
            .forEach { removeNRule(it) }

        //then productivity
        val productiveSymbols : MutableSet<NSymbol> = mutableSetOf()
        productiveSymbols.addAll(grammar.tRules.map { it.left })

        val productiveRules : MutableSet<NRule> = mutableSetOf()
        var newRules: MutableSet<NRule>

        //checking performs in the iterative manner
        do {
            newRules = grammar.nRules
                .filter { !productiveRules.contains(it) }
                .filter { productiveSymbols.contains(it.getRightFirst()) && productiveSymbols.contains(it.getRightSecond()) }
                .toMutableSet()

            productiveRules.addAll(newRules)
            productiveSymbols.addAll(newRules.map { it.left })
        } while (newRules.isNotEmpty())

        //now we remove unproductive rules
        grammar.nRules
            .filter { !productiveRules.contains(it) }
            .forEach { removeNRule(it) }
    }

    private fun removeUnusedSymbols(){
        //on start, all symbols are unused
        val unusedSymbols : MutableSet<NSymbol> = mutableSetOf()
        unusedSymbols.addAll(grammar.nSymbols)

        //then we iterate through rules and remove used ones
        unusedSymbols.removeAll(
            grammar.tRules
                .map { it.left }
                .toSet())

        unusedSymbols.removeAll(
            grammar.nRules
                .flatMap { listOf(it.left, it.getRightFirst(), it.getRightSecond()) }
                .toSet())

        //it's time to say goodbye
        unusedSymbols.forEach { removeNSymbol(it) }
    }

    //set manipulation methods
    private fun addNRule(rule : NRule) {
        grammar.nRules.add(rule)
    }

    private fun removeNRule(rule : NRule){
        grammar.nRules.remove(rule)
    }

    private fun addNSymbol(symbol : NSymbol) {
        grammar.nSymbols.add(symbol)
    }

    private fun removeNSymbol(symbol : NSymbol){
        grammar.nSymbols.remove(symbol)
    }

    //helper symbols functions


    private fun rulesWith(left : NSymbol? = null, first : NSymbol? = null, second : NSymbol? = null) : MutableSet<NRule> {
        return grammar.nRules
            .filter {
                (left==null || it.left == left) &&
                        (first==null || it.getRightFirst() == first) &&
                        (second==null || it.getRightSecond() == second) }
            .toMutableSet()
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