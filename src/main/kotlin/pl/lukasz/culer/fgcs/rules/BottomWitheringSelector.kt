package pl.lukasz.culer.fgcs.rules

import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.rules.base.WitheringSelector
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.ReversedMembershipSelector

/**
 * Remove 1 rule from bottomWithering_maxNumber rules with lowest membership. Rule is selected randomly,
 * with weight adjusted to membership if bottomWithering_proportionalProbability = true and uniformly if false.
 * "perfect" rules (membership = 1.0) are not considered. If all rules are perfect, then none of them are removed.
 */
//@TODO UT
class BottomWitheringSelector : WitheringSelector(){
    //region consts
    companion object {
        const val REASON_BOTTOM_WITHERING = "Rule withered (Bottom)"
    }
    //endregion
    //region override section
    override fun applyWithering(grammarController: GrammarController): Boolean {
        val consideredRules = getRulesToSelectFrom(grammarController)
        if(consideredRules.isEmpty()) return false //no rules will be removed

        if(!grammarController.settings.bottomWitheringProportionalProbability) removeRule(grammarController, consideredRules.random())
        else {
            removeRule(grammarController,
                ReversedMembershipSelector(consideredRules) {it.membership.midpoint}
                    .getRandomly())
        }
        return true
    }
    //endregion
    //region private stuff
    private fun getRulesToSelectFrom(grammarController: GrammarController) =
        grammarController.grammar.nRules
        .sortedBy { it.membership }     //lowest membership
        .take(grammarController.settings.bottomWitheringMaxNumber)  //taking max number
        .filter { it.membership < Consts.FULL_MEMBERSHIP }      //no perfect rules!

    private fun removeRule(grammarController: GrammarController, rule : NRule?){
        rule?.let { grammarController.removeNRule(rule, REASON_BOTTOM_WITHERING) }
    }
    //endregion
}