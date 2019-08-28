package pl.lukasz.culer.data

import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber

class TestExample(var sequence : String = "",
                  var membership : IntervalFuzzyNumber = IntervalFuzzyNumber(0.0)) {

    init {
        sequence = sequence.toLowerCase()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestExample

        if (sequence != other.sequence) return false

        return true
    }

    override fun hashCode(): Int {
        return sequence.hashCode()
    }


}