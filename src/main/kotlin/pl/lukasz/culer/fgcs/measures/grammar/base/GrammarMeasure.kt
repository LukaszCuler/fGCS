package pl.lukasz.culer.fgcs.measures.grammar.base

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.Grammar

abstract class GrammarMeasure {
    /**
     * region abstract methods
     */
    abstract fun getDoubleMeasure(grammar : Grammar, examples :List<FGCS.ExampleAnalysisResult>) : Double             //double measure that express fitness of the grammar
    abstract fun isGrammarPerfect(grammar : Grammar, examples :List<FGCS.ExampleAnalysisResult>) : Boolean             //should we exit simulation?
    //endregion

    /**
     * region public, general methods
     */
    fun getComparator(examples :List<FGCS.ExampleAnalysisResult>) = Comparator<Grammar> { g1, g2 ->
        getDoubleMeasure(g1, examples).compareTo(getDoubleMeasure(g2, examples))
    }
    //endregion
}