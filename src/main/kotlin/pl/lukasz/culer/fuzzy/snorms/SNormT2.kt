package pl.lukasz.culer.fuzzy.snorms

import pl.lukasz.culer.fuzzy.FuzzyOperator
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

enum class SNormT2 : FuzzyOperator {
    MAX {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(SNorm.MAX(num.map { it.lowerBound }.toTypedArray()), SNorm.MAX(num.map { it.upperBound }.toTypedArray()))
    },

    DRASTIC {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(SNorm.DRASTIC(num.map { it.lowerBound }.toTypedArray()), SNorm.DRASTIC(num.map { it.upperBound }.toTypedArray()))
    },

    LUKASIEWICZ {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(SNorm.LUKASIEWICZ(num.map { it.lowerBound }.toTypedArray()), SNorm.LUKASIEWICZ(num.map { it.upperBound }.toTypedArray()))
    },

    SUM {
        override fun invoke(vararg num : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(SNorm.SUM(num.map { it.lowerBound }.toTypedArray()), SNorm.SUM(num.map { it.upperBound }.toTypedArray()))
    }
}