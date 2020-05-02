package pl.lukasz.culer.fuzzy.tnorms

import kotlin.math.pow

enum class TNorm : (Array<Double>) -> Double {
    MIN {
        override fun invoke(nums : Array<Double>) = nums.min() ?: 0.0
    },

    MIN_SQRT {
        override fun invoke(nums : Array<Double>) = MIN.invoke(nums).pow(1.0/nums.size.toDouble())
    },

    LUKASIEWICZ {
        override fun invoke(nums : Array<Double>) = nums.reduce(this::two)

        private fun two(a: Double, b: Double): Double {
            val value = a + b - 1
            return if (0 > value) value else 0.0
        }
    },

    PROD {
        override fun invoke(nums : Array<Double>) = nums.reduce { a, b -> a*b }
    },

    DRASTIC {
        override fun invoke(nums : Array<Double>) = nums.reduce(this::two)

        private fun two(a: Double, b: Double): Double {
            if (a == 1.0) return b
            if (b == 1.0) return a
            return 0.0
        }
    }
}