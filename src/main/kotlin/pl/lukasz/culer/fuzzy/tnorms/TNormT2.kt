package pl.lukasz.culer.fuzzy.tnorms

import pl.lukasz.culer.fuzzy.FuzzyOperator
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

enum class TNormT2 : FuzzyOperator {
    MIN {
        override fun invoke(num : Array<IntervalFuzzyNumber>) =
            IntervalFuzzyNumber(TNorm.MIN(num.map { it.lowerBound }.toTypedArray()), TNorm.MIN(num.map { it.upperBound }.toTypedArray()))
    },

    LUKASIEWICZ {
        override fun invoke(num : Array<IntervalFuzzyNumber>) =
            IntervalFuzzyNumber(TNorm.LUKASIEWICZ(num.map { it.lowerBound }.toTypedArray()), TNorm.LUKASIEWICZ(num.map { it.upperBound }.toTypedArray()))
    },

    PROD {
        override fun invoke(num : Array<IntervalFuzzyNumber>) =
            IntervalFuzzyNumber(TNorm.PROD(num.map { it.lowerBound }.toTypedArray()), TNorm.PROD(num.map { it.upperBound }.toTypedArray()))
    },

    DRASTIC {
        override fun invoke(num : Array<IntervalFuzzyNumber>) =
            IntervalFuzzyNumber(TNorm.DRASTIC(num.map { it.lowerBound }.toTypedArray()), TNorm.DRASTIC(num.map { it.upperBound }.toTypedArray()))
    }
}