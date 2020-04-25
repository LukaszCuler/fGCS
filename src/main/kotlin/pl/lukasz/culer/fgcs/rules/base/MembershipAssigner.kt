package pl.lukasz.culer.fgcs.rules.base

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.Grammar

abstract class MembershipAssigner {
    //we have to assign to all at once - due to possible complexity
    abstract fun assignMemberships(grammar: Grammar, examples :List<FGCS.ExampleAnalysisResult>)
}