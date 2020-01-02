package pl.lukasz.culer.fuzzy.processors.relevance

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.F
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.fuzzy.processors.heatmap.MaxMembershipHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessorFactory
import pl.lukasz.culer.fuzzy.processors.relevance.MembershipProportionalRelevanceProcessor

@RunWith(MockitoJUnitRunner::class)
class MembershipProportionalRelevanceProcessorTests {
    lateinit var processor : MembershipProportionalRelevanceProcessor
    @Before
    fun init(){
        processor = MembershipProportionalRelevanceProcessor()
    }

    @Test
    fun assignTest(){
        //data preparation
        val treeA = MultiParseTreeNode.SubTreePair(classificationMembership = F(0.5))
        val treeB = MultiParseTreeNode.SubTreePair(classificationMembership = F(0.25))
        val treeC = MultiParseTreeNode.SubTreePair(classificationMembership = F(0.0))

        val children: MutableList<MultiParseTreeNode.SubTreePair> = mutableListOf(treeA, treeB, treeC)

        //test!
        processor.assignRelevanceToVariants(children)

        //verification
        Assert.assertEquals(F(1.0), treeA.relevance)
        Assert.assertEquals(F(0.5), treeB.relevance)
        Assert.assertEquals(F(0.0), treeC.relevance)
    }

}