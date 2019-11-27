package pl.lukasz.culer.fuzzy.processors.relevance.base

import pl.lukasz.culer.fuzzy.processors.relevance.MembershipProportionalRelevanceProcessor
import pl.lukasz.culer.fuzzy.processors.relevance.WTARelevanceProcessor

enum class RelevanceProcessorFactory : () -> RelevanceProcessor {
    WTA {
        override fun invoke() = WTARelevanceProcessor()
    },
    MEMB_PROP {
        override fun invoke() = MembershipProportionalRelevanceProcessor()
    }
}