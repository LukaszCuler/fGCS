package pl.lukasz.culer.fgcs.models

import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol

class CYKTable(val example: TestExample) {
    //parsing-related variables
    var recentlyModified = false
    val privateRuleSet : MutableSet<NRule> = mutableSetOf()

    //cykTable[y][x]
    val cykTable : Array<Array<MutableSet<NSymbol>>> = Array(example.size) {
        Array(example.size) {
            mutableSetOf<NSymbol>()
        }
    }
}