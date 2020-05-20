package pl.lukasz.culer.fgcs.rules

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.rules.base.MembershipAssigner
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.RxUtils
import java.util.concurrent.atomic.DoubleAdder

/**
 * Simple -> doesn't consider type-2
 */
//@TODO UT
class SimpleOccurrenceMembershipAssigner : MembershipAssigner() {
    companion object {
        const val POSITIVE_ACCUMULATED = "POSITIVE_ACCUMULATED"
        const val NEGATIVE_ACCUMULATED = "NEGATIVE_ACCUMULATED"
    }

    //region extension properties
    private fun NRule.initializePositiveAccs() {
        analysisTemps[POSITIVE_ACCUMULATED] = DoubleAdder()
    }

    fun NRule.getPositiveAccs() = analysisTemps[POSITIVE_ACCUMULATED] as DoubleAdder

    private fun NRule.initializeNegativeAccs() {
        analysisTemps[NEGATIVE_ACCUMULATED] = DoubleAdder()
    }

    fun NRule.getNegativeAccs() = analysisTemps[NEGATIVE_ACCUMULATED] as DoubleAdder
    //endregion
    //region overrides
    override fun initialize() {
        grammarController?.grammar?.nRules?.forEach {
            it.initializePositiveAccs()
            it.initializeNegativeAccs()
        }
    }

    override fun analyzeExample(exampleContainer: FGCS.ExampleAnalysisResult) {
        val occurredRules = mutableSetOf<NRule>()
        parseTreeController?.processRootToNodesAll(exampleContainer.multiParseTreeNode) { node ->
            node.subtrees.forEach { variant ->
                grammarController?.nRulesWith(node.node, variant.subTreePair.first.node, variant.subTreePair.second.node)
                    ?.single()
                    ?.let { occurredRules.add(it) }
            }
        }
        val posAdd = exampleContainer.example.explicitMembership.midpoint
        val negAdd = Consts.FULL_MEMBERSHIP.midpoint - posAdd
        occurredRules.forEach {
            it.getPositiveAccs().add(posAdd)
            it.getNegativeAccs().add(negAdd)
        }
    }

    override fun finalize() {
        //we translate occurances into membership
        val nRules = grammarController?.grammar?.nRules?.toList() ?: return

        RxUtils.computeParallelly(nRules) {
            val positive = it.getPositiveAccs().sum()
            val sum = positive + it.getNegativeAccs().sum()
            it.membership = F(positive/sum)
        }
    }
    //endregion
    //region private stuff
    //endregion
}