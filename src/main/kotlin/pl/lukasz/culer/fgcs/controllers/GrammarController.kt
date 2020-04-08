package pl.lukasz.culer.fgcs.controllers

import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
import pl.lukasz.culer.fgcs.models.rules.TRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.DEFAULT_START_SYMBOL
import kotlin.random.Random

class GrammarController(val settings : Settings) {
    /**
     * region params
     * **/
    lateinit var grammar : Grammar
    var learningData : List<TestExample> = mutableListOf()
    var testData : List<TestExample>? = null
    //endregion

    /**
     * region constructors
     **/
    //creating grammar from data
    constructor(settings : Settings, learningData : List<TestExample>, testData : List<TestExample>? = null) : this(settings) {
        this.learningData = learningData
        this.testData = testData
        createGrammarFromData()
    }

    //simply loading grammar
    constructor(settings : Settings, grammar: Grammar, testData : List<TestExample>? = null) : this(settings) {
        //grammar is ready, just needs some refreshment
        this.grammar = grammar
        updateSymbolReferences()

        //but we have to take care of data
        this.testData = testData
        parseTestData()
    }
    //endregion

    /**
     * region public methods
     **/

    //endregion

    /**
     * region private methods
     **/
    //constructing grammar
    private fun createGrammarFromData(){
        grammar = Grammar() //newborn

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

        //now we can parse examples
        for(example in learningData) {
            example.parse(this)
        }

        parseTestData()
    }

    private fun updateSymbolReferences(){
        //should be probably done in deserializer...
        //processing terminal rules
        val newTRules : MutableSet<TRule> = mutableSetOf()
        for(trule in grammar.tRules){
            val validLeft = findNSymbolByChar(trule.left.symbol)
            val validRight = findTSymbolByChar(trule.getRight().symbol)

            if(validLeft!=null && validRight!=null) newTRules.add(TRule(validLeft, validRight))
        }

        grammar.tRules.clear()
        grammar.tRules.addAll(newTRules)

        //processing non-terminal rules
        val newNRules : MutableSet<NRule> = mutableSetOf()
        for(nrule in grammar.nRules){
            val validLeft = findNSymbolByChar(nrule.left.symbol)
            val validRightFirst = findNSymbolByChar(nrule.getRightFirst().symbol)
            val validRightSecond = findNSymbolByChar(nrule.getRightSecond().symbol)

            if(validLeft!=null && validRightFirst != null && validRightSecond != null)
                newNRules.add(NRule(validLeft, NRuleRHS(validRightFirst, validRightSecond), nrule.membership))
        }

        grammar.nRules.clear()
        grammar.nRules.addAll(newNRules)

        //start symbol
        val startSymbol = findNSymbolByChar(grammar.starSymbol.symbol)
        if(startSymbol!=null) grammar.starSymbol = startSymbol
    }

    //cleaning stuff
    fun removeUnreachableAndUnproductiveRules(){
        //first we have to deal witch achievability

        val achievableRules : MutableSet<NRule> = mutableSetOf()
        //symbols first
        val achievableSymbols = mutableSetOf(grammar.starSymbol)
        var recentlyAdded = mutableSetOf(grammar.starSymbol)
        while (recentlyAdded.isNotEmpty()){
            recentlyAdded = recentlyAdded
                .flatMap { nRulesWith(left = it) }
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

    fun removeUnusedSymbols(){
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
    fun addNRule(rule : NRule) {
        grammar.nRules.add(rule)
    }

    fun removeNRule(rule : NRule){
        grammar.nRules.remove(rule)
    }

    fun addNSymbol(symbol : NSymbol) {
        grammar.nSymbols.add(symbol)
    }

    fun removeNSymbol(symbol : NSymbol){
        grammar.nSymbols.remove(symbol)
    }

    fun containsNRule(rule : NRule) = grammar.nRules.contains(rule)

    //helper symbols functions

    fun nRulesWith(left : NSymbol? = null,
                   first : NSymbol? = null,
                   second : NSymbol? = null,
                   extendRules : Set<NRule> = setOf()) : MutableSet<NRule> {
        return grammar.nRules
            .union(extendRules)
            .filter {
                (left==null || it.left == left) &&
                        (first==null || it.getRightFirst() == first) &&
                        (second==null || it.getRightSecond() == second) }
            .toMutableSet()
    }

    fun tRulesWith(left : NSymbol? = null, terminal : TSymbol? = null) : MutableSet<TRule> {
        return grammar.tRules
            .filter {
                (left==null || it.left == left) &&
                        (terminal==null || it.getRight() == terminal) }
            .toMutableSet()
    }

    fun setStartSymbol(newStartSymbol : NSymbol) {
        grammar.starSymbol.isStartSymbol = false
        newStartSymbol.isStartSymbol = true

        grammar.starSymbol = newStartSymbol
        addNSymbol(newStartSymbol)
    }

    fun getNewNSymbol() : NSymbol?{
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

    fun findTSymbolByChar(symbolChar : Char) : TSymbol? {
        return grammar.tSymbols.find { it.symbol == symbolChar }
    }

    fun findNSymbolByChar(symbolChar : Char) : NSymbol? {
        return grammar.nSymbols.find { it.symbol == symbolChar }
    }

    fun getRandomNSymbol(symbolsToDrawFrom : MutableSet<NSymbol> = grammar.nSymbols) =
        symbolsToDrawFrom.toList()[Random.nextInt(symbolsToDrawFrom.size)]

    fun getNewOrExistingNSymbolRandomly(symbolsToDrawFrom : MutableSet<NSymbol> = grammar.nSymbols) : NSymbol {
        when(settings.newSymbolSelectionMethod) {
            NewSymbolSelectionMethod.INCREMENTAL -> {
                val drawnValue = Random.nextDouble(symbolsToDrawFrom.size.toDouble()+settings.newSymbolCoef)
                return if(drawnValue<=settings.newSymbolCoef) {
                    getNewNSymbol() ?: getRandomNSymbol(symbolsToDrawFrom)
                } else {
                    getRandomNSymbol(symbolsToDrawFrom)
                }
            }

            NewSymbolSelectionMethod.FIXED -> {
                return if(Random.nextDouble(settings.newSymbolCoef)<=settings.newSymbolCoef) {
                    getNewNSymbol() ?: getRandomNSymbol(symbolsToDrawFrom)
                } else {
                    getRandomNSymbol(symbolsToDrawFrom)
                }
            }
        }
    }

    private fun parseTestData(){
        if(testData!=null) {
            for(example in testData!!) {
                example.parse(this)
            }
        }
    }
    //endregion
    //region structures
    enum class NewSymbolSelectionMethod {
        INCREMENTAL, FIXED  //no need for factory etc. unlikely to modify
    }
    //endregion
}