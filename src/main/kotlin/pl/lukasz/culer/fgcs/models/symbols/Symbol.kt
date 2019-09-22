package pl.lukasz.culer.fgcs.models.symbols

import com.google.gson.annotations.SerializedName

abstract class Symbol(
    @SerializedName("symbol")
    val symbol : Char,
    @SerializedName("isTerminal")
    val isTerminal : Boolean,
    @SerializedName("isStartSymbol")
    var isStartSymbol : Boolean = false){

    //boilerplate stuff
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Symbol

        if (symbol != other.symbol) return false
        if (isTerminal != other.isTerminal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbol.hashCode()
        result = 31 * result + isTerminal.hashCode()
        return result
    }
}