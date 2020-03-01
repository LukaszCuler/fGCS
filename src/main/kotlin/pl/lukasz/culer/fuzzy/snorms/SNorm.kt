package pl.lukasz.culer.fuzzy.snorms

enum class SNorm : (Array<Double>) -> Double {
    MAX {
        override fun invoke(nums : Array<Double>) = nums.max() ?: 0.0
    },

    DRASTIC {
        override fun invoke(nums : Array<Double>) = nums.reduce(this::two)

        private fun two(a: Double, b: Double) : Double {
            if(a==0.0) return b
            if(b==0.0) return a
            return 1.0
        }
    },

    LUKASIEWICZ {
        override fun invoke(nums : Array<Double>) = nums.reduce(this::two)

        private fun two(a: Double, b: Double): Double {
            val sumValue = a + b
            return if (sumValue < 1) sumValue else 1.0
        }
    },

    SUM {
        override fun invoke(nums : Array<Double>) = nums.reduce(this::two)

        private fun two(a: Double, b: Double): Double = a + b - a * b
    }
}