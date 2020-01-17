package pl.lukasz.culer.fgcs.models

import com.google.gson.annotations.SerializedName
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.TRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol

class Grammar(
    @SerializedName("nonTerminalRules")
    val nRules : MutableSet<NRule> = mutableSetOf(),
    @SerializedName("terminalRules")
    val tRules : MutableSet<TRule> = mutableSetOf(),

    @SerializedName("nonTerminalSymbols")
    val nSymbols : MutableSet<NSymbol> = mutableSetOf(),
    @SerializedName("terminalSymbols")
    val tSymbols : MutableSet<TSymbol> = mutableSetOf()) {

    @SerializedName("startSymbol")
    lateinit var starSymbol : NSymbol

    //note - symbols are not copied, since they have no iteration-dependent values
    fun copy() : Grammar {
        val newGrammar = Grammar()
        newGrammar.nRules.addAll(nRules.map { it.copy() })
        newGrammar.tRules.addAll(tRules.map { it.copy() })
        newGrammar.nSymbols.addAll(nSymbols)
        newGrammar.tSymbols.addAll(tSymbols)
        newGrammar.starSymbol = starSymbol
        return newGrammar
    }
}