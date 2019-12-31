package pl.lukasz.culer.fuzzy.processors.heatmap.base

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.fuzzy.processors.heatmap.MaxMembershipHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.RelavanceWeightedHeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessorFactory

@RunWith(MockitoJUnitRunner::class)
class HeatmapProcessorFactoryTests {
    @Test
    fun minMaxTest(){
        Assert.assertTrue(HeatmapProcessorFactory.MINMAX() is MaxMembershipHeatmapProcessor)
    }

    @Test
    fun relevanceWeightedTest(){
        Assert.assertTrue(HeatmapProcessorFactory.RELEVANCE_WEIGHTED() is RelavanceWeightedHeatmapProcessor)
    }
}