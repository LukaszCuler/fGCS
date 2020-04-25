package pl.lukasz.culer.fgcs.rules.base

import pl.lukasz.culer.fgcs.controllers.GrammarController

abstract class GrammarPostProcessor {
    //may be more than one, so should be handled by selector. Returns true if removed
    abstract fun applyOperators(grammarController: GrammarController) : Boolean
}