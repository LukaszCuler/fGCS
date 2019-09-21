package pl.lukasz.culer.fuzzy.memberships

import kotlin.math.min

enum class SubtreeMembership : (Double, Double, Double) -> Double {
    MIN {
        override fun invoke(a: Double, b: Double, c : Double) = min(min(a,b),c)
    },
    MIN_SQRT {
        override fun invoke(a: Double, b: Double, c : Double) = kotlin.math.sqrt(MIN.invoke(a,b,c))
    }
}