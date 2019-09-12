package pl.lukasz.culer.fgcs.models

import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.TRule

class Grammar(
    val nRules : MutableList<NRule> = mutableListOf(),
    val tRules : MutableList<TRule> = mutableListOf()) {
}