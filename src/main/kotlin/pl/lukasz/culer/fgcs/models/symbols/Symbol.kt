package pl.lukasz.culer.fgcs.models.symbols

abstract class Symbol(val symbol : Char, val isTerminal : Boolean, var isStartSymbol : Boolean = false){

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