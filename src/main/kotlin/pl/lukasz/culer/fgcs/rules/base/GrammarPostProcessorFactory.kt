package pl.lukasz.culer.fgcs.rules.base

import pl.lukasz.culer.fgcs.rules.GAPostProcessor

enum class GrammarPostProcessorFactory : () -> GrammarPostProcessor {
    GENETIC_ALGORITHMS {
        override fun invoke(): GrammarPostProcessor = GAPostProcessor()
    }
}