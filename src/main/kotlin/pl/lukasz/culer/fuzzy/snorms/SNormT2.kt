package pl.lukasz.culer.fuzzy.snorms

import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

enum class SNormT2 : (IntervalFuzzyNumber,IntervalFuzzyNumber) -> IntervalFuzzyNumber {
    MAX {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber) =
            IntervalFuzzyNumber(SNorm.MAX(a.lowerBound, b.lowerBound), SNorm.MAX(a.upperBound, b.upperBound))
    },

    DRASTIC {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber) =
            IntervalFuzzyNumber(SNorm.DRASTIC(a.lowerBound, b.lowerBound), SNorm.DRASTIC(a.upperBound, b.upperBound))
    },

    LUKASIEWICZ {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber) =
            IntervalFuzzyNumber(SNorm.LUKASIEWICZ(a.lowerBound, b.lowerBound), SNorm.LUKASIEWICZ(a.upperBound, b.upperBound))
    },

    SUM {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber) =
            IntervalFuzzyNumber(SNorm.SUM(a.lowerBound, b.lowerBound), SNorm.SUM(a.upperBound, b.upperBound))
    }
}