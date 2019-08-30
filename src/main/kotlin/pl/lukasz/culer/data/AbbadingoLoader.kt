package pl.lukasz.culer.data

import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.ABBADINGO_INVALID_EXAMPLE
import pl.lukasz.culer.utils.ABBADINGO_INVALID_HEADER
import pl.lukasz.culer.utils.Logger
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


class AbbadingoLoader {
    companion object {
        const val L1 = "./TestSets/l1.txt"
        const val L2 = "./TestSets/l2.txt"
        const val L5 = "./TestSets/l5.txt"
        const val TOY = "./TestSets/toy.txt"
        const val AnBn = "./TestSets/AnBn.txt"

        const val TAG = "AbbadingoLoader"


        fun loadAbbadingoTestSet(filePath : String) : List<TestExample> {
            val reader: BufferedReader
            try {
                reader = BufferedReader(FileReader(filePath))
                val firstLine: String = reader.readLine()

                //general parameters of file
                val firstLineValues = firstLine.split(' ')
                if(firstLineValues.isEmpty()) return returnWithError(ABBADINGO_INVALID_HEADER.format(filePath))

                val numberOfExamples = firstLineValues[0].toIntOrNull() ?: return returnWithError(ABBADINGO_INVALID_HEADER.format(filePath))

                val testSet : MutableList<TestExample> = mutableListOf()
                for(i in 1..numberOfExamples){
                    val exampleLine = reader.readLine() ?: return returnWithError(ABBADINGO_INVALID_EXAMPLE.format(i, filePath), testSet)
                    val exampleSegments = exampleLine.split(' ')
                    if(exampleSegments.size < 2) return returnWithError(ABBADINGO_INVALID_EXAMPLE.format(i, filePath), testSet)

                    val membershipValue = exampleSegments[0].toDoubleOrNull() ?: return returnWithError(ABBADINGO_INVALID_EXAMPLE.format(i, filePath), testSet)
                    val sequence = exampleSegments.subList(1, exampleSegments.lastIndex).joinToString().toLowerCase()
                    testSet.add(TestExample(sequence, IntervalFuzzyNumber(membershipValue)))
                }
                reader.close()
                return testSet
            } catch (e: IOException) {
                e.printStackTrace()
                return listOf()
            }
        }

        private fun returnWithError(message : String, testSet : List<TestExample> = listOf()) : List<TestExample> {
            Logger.instance.e(TAG, message)
            return testSet
        }
    }
}