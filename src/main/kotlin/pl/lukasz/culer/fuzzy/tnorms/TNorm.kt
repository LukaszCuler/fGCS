package pl.lukasz.culer.fuzzy.tnorms

enum class TNorm : (Double,Double) -> Double {
    MIN {
        override fun invoke(a: Double, b: Double) = if (a < b) a else b
    },

    LUKASIEWICZ {
        override fun invoke(a: Double, b: Double): Double {
            val value = a + b - 1
            return if (0 > value) value else 0.0
        }
    },

    PROD {
        override fun invoke(a: Double, b: Double) = a * b
    },

    DRASTIC {
        override fun invoke(a: Double, b: Double): Double {
            if (a == 1.0) return b
            if (b == 1.0) return a
            return 0.0
        }
    }
}