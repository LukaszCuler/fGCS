package pl.lukasz.culer.fgcs.measures.grammar.base

import pl.lukasz.culer.fgcs.models.Grammar

abstract class GrammarMeasure {
    /**
     * region abstract methods
     */
    abstract fun getDoubleMeasure(grammar : Grammar) : Double             //double measure that express fitness of the grammar
    abstract fun isGrammarPerfect(grammar : Grammar) : Boolean             //should we exit simulation?
    //endregion

    /**
     * region public, general methods
     */
    fun getComparatorForMeasure(){
        
    }
    //endregion
}