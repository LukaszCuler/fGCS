package pl.lukasz.culer.data

import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.symbols.TSymbol
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

class TestExample(var sequence : String = "",
                  var explicitMembership : IntervalFuzzyNumber = F(0.0)) {    //membership for learning set, defined by user
    //shortcut to sequence size
    val size get() = sequence.length

    var parsedSequence : Array<TSymbol> = arrayOf()

    init {
        sequence = sequence.toLowerCase()
    }

    fun parse(grammarController: GrammarController){
        val parsedExample : MutableList<TSymbol> = mutableListOf()
        for(unparsedTerminal in sequence){
            val parsedTerminal = grammarController.findTSymbolByChar(unparsedTerminal)
            if(parsedTerminal!=null) parsedExample.add(parsedTerminal)
        }
        parsedSequence = Array(parsedExample.size) {
            parsedExample[it]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestExample

        if (sequence != other.sequence) return false

        return true
    }

    override fun hashCode(): Int {
        return sequence.hashCode()
    }


}