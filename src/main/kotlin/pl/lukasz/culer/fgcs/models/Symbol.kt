package pl.lukasz.culer.fgcs.models

data class Symbol(val symbol : Char, val isTerminal : Boolean, val isStartSymbol : Boolean = false)