package pl.lukasz.culer.fuzzy.memberships

import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import kotlin.math.min

@Deprecated("To replace")
enum class SubtreeMembershipT2 : (IntervalFuzzyNumber, IntervalFuzzyNumber, IntervalFuzzyNumber) -> IntervalFuzzyNumber {
    MIN {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber, c : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(
                SubtreeMembership.MIN(a.lowerBound, b.lowerBound, c.lowerBound),
                SubtreeMembership.MIN(a.upperBound, b.upperBound, c.upperBound))
    },

    MIN_SQRT {
        override fun invoke(a: IntervalFuzzyNumber, b: IntervalFuzzyNumber, c : IntervalFuzzyNumber) =
            IntervalFuzzyNumber(
                SubtreeMembership.MIN_SQRT(a.lowerBound, b.lowerBound, c.lowerBound),
                SubtreeMembership.MIN_SQRT(a.upperBound, b.upperBound, c.upperBound))
    }
}