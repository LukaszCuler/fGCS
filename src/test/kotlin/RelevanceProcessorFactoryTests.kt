import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.lukasz.culer.fuzzy.processors.relevance.MembershipProportionalRelevanceProcessor
import pl.lukasz.culer.fuzzy.processors.relevance.WTARelevanceProcessor
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessorFactory

@RunWith(MockitoJUnitRunner::class)
class RelevanceProcessorFactoryTests {
    @Test
    fun wtaRelevanceTest() {
        Assert.assertTrue(RelevanceProcessorFactory.WTA() is WTARelevanceProcessor)
    }

    @Test
    fun membershipProportionalRelevanceTest() {
        Assert.assertTrue(RelevanceProcessorFactory.MEMB_PROP() is MembershipProportionalRelevanceProcessor)
    }
}