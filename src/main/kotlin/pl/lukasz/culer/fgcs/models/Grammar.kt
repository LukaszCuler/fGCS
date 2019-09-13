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

    //help variables
    @Transient
    lateinit var nRulesMap: MutableMap<NSymbol, MutableMap<NSymbol, MutableMap<NSymbol, NRule?>>>
}