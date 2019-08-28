package pl.lukasz.culer.fuzzy.tnorms

import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

enum class TNormT2 : (IntervalFuzzyNumber, IntervalFuzzyNumber) -> IntervalFuzzyNumber {
    MIN {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber) =
            IntervalFuzzyNumber(TNorm.MIN(a.lowerBound, b.lowerBound), TNorm.MIN(a.upperBound, b.upperBound))
    },

    LUKASIEWICZ {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber) =
            IntervalFuzzyNumber(TNorm.LUKASIEWICZ(a.lowerBound, b.lowerBound), TNorm.LUKASIEWICZ(a.upperBound, b.upperBound))
    },

    PROD {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber) =
            IntervalFuzzyNumber(TNorm.PROD(a.lowerBound, b.lowerBound), TNorm.PROD(a.upperBound, b.upperBound))
    },

    DRASTIC {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber) =
            IntervalFuzzyNumber(TNorm.DRASTIC(a.lowerBound, b.lowerBound), TNorm.DRASTIC(a.upperBound, b.upperBound))
    }
}