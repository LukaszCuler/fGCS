package pl.lukasz.culer.fgcs.models

import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol

typealias CYKCell = MutableSet<NSymbol>

class CYKTable(val example: TestExample) {
    //parsing-related variables
    var recentlyModified = false
    val privateRuleSet : MutableSet<NRule> = mutableSetOf()

    //cykTable[y][x]
    val cykTable : Array<Array<CYKCell>> = Array(example.size) {
        Array(example.size) {
            object : LinkedHashSet<NSymbol>() {
                override fun add(element: NSymbol): Boolean {
                    return super.add(element).also { if(it) recentlyModified = true }   //listener for changes
                }

                override fun remove(element: NSymbol): Boolean {
                    return super.remove(element).also { if(it) recentlyModified = true }   //listener for changes
                }
            } as MutableSet<NSymbol>
        }
    }

    //helper shortcuts
    val size get() = example.size
    val lastIndex get() = size-1
    val unitExample get() = size == 1
    val rootCell get() = cykTable[lastIndex][0]
}