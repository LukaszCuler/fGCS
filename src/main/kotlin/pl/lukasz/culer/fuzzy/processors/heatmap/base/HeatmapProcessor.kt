package pl.lukasz.culer.fuzzy.processors.heatmap.base

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode

interface HeatmapProcessor {
    fun getSubtreeToDisplay(treeNode : MultiParseTreeNode) : MultiParseTreeNode.SubTreePair

}