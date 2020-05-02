package pl.lukasz.culer.utils

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import kotlin.random.Random

/**
 * Weight of selection is reversed to membership -> for 1.0 membership weight is equal to 0.0, for 0.0 -> 1.0
 */
class ReversedMembershipSelector<T>(val list : List<T>, val membershipAssign : (T) -> Double) {
    fun getRandomly() : T? {
        val probabilityTab = mutableListOf<Pair<Double, T>>()
        var currentSum = 0.0
        for(element in list){
            currentSum += membershipAssign(element)
            probabilityTab.add(currentSum to element)
        }

        //selecting subtree based on probability tab
        val drawnNumber = Random.nextDouble(probabilityTab.last().first)
        var selectedElement : T? = null

        for(i in probabilityTab.indices){
            if(drawnNumber <= probabilityTab[i].first){
                selectedElement = probabilityTab[i].second
                break
            }
        }

        return selectedElement
    }
}