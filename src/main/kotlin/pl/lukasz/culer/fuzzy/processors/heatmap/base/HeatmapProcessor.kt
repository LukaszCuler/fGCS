package pl.lukasz.culer.fuzzy.processors.heatmap.base

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings

interface HeatmapProcessor {
    fun assignDerivationMembershipToVariants(inhValue : IntervalFuzzyNumber?,
                                             children: MutableList<MultiParseTreeNode.SubTreePair>,
                                             settings: Settings)
}