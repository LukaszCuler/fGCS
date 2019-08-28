package pl.lukasz.culer.fuzzy.snorms

enum class SNorm : (Double,Double) -> Double {
    MAX {
        override fun invoke(a: Double, b: Double) = if(a > b) a else b
    },

    DRASTIC {
        override fun invoke(a: Double, b: Double) : Double {
            if(a==0.0) return b
            if(b==0.0) return a
            return 1.0
        }
    },

    LUKASIEWICZ {
        override fun invoke(a: Double, b: Double): Double {
            val sumValue = a + b
            return if (sumValue < 1) sumValue else 1.0
        }
    },

    SUM {
        override fun invoke(a: Double, b: Double): Double = a + b - a * b
    }
}