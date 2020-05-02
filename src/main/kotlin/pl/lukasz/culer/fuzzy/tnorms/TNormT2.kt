package pl.lukasz.culer.fuzzy.tnorms

import pl.lukasz.culer.fuzzy.FuzzyOperator
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

enum class TNormT2 : FuzzyOperator {
    MIN {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(TNorm.MIN(num.map { it.lowerBound }.toTypedArray()), TNorm.MIN(num.map { it.upperBound }.toTypedArray()))
    },

    MIN_SQRT {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(
                TNorm.MIN_SQRT(num.map { it.lowerBound }.toTypedArray()),
                TNorm.MIN_SQRT(num.map { it.upperBound }.toTypedArray()))
    },

    LUKASIEWICZ {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(TNorm.LUKASIEWICZ(num.map { it.lowerBound }.toTypedArray()), TNorm.LUKASIEWICZ(num.map { it.upperBound }.toTypedArray()))
    },

    PROD {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(TNorm.PROD(num.map { it.lowerBound }.toTypedArray()), TNorm.PROD(num.map { it.upperBound }.toTypedArray()))
    },

    DRASTIC {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(TNorm.DRASTIC(num.map { it.lowerBound }.toTypedArray()), TNorm.DRASTIC(num.map { it.upperBound }.toTypedArray()))
    }
}