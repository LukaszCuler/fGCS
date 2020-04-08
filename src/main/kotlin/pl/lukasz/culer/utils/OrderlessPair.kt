package pl.lukasz.culer.utils

class OrderlessPair<T>(val first: T, val second: T) {
    fun contains(obj : T) = first == obj || second == obj

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderlessPair<*>

        return (first == other.first && second == other.second) ||
                (first == other.second && second == other.first)
    }

    override fun hashCode(): Int {
        var resultOne = first?.hashCode() ?: 0
        resultOne = 31 * resultOne + (second?.hashCode() ?: 0)

        var resultTwo = second?.hashCode() ?: 0
        resultTwo = 31 * resultTwo + (first?.hashCode() ?: 0)

        return (resultOne+resultTwo)/2
    }


}