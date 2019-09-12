package pl.lukasz.culer.fgcs.models.symbols

abstract class Symbol(val symbol : Char, val isTerminal : Boolean, var isStartSymbol : Boolean = false)