package pl.lukasz.culer.fgcs.rules.base

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.Grammar

abstract class WitheringSelector {
    //may be more than one, so should be handled by selector. Returns true if removed
    abstract fun applyWithering(grammarController: GrammarController) : Boolean
}