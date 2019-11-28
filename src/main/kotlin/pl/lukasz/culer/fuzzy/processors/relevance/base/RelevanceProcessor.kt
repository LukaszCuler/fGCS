package pl.lukasz.culer.fuzzy.processors.relevance.base

import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode

interface RelevanceProcessor {
    fun assignRelevanceToVariants(children: MutableList<MultiParseTreeNode.SubTreePair>)
}