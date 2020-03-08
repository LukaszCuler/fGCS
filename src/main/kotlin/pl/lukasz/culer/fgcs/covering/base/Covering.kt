package pl.lukasz.culer.fgcs.covering.base

import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.controllers.ParseTreeController
import pl.lukasz.culer.fgcs.models.CYKTable

abstract class Covering(val table: CYKTable,
                        val grammarController: GrammarController,
                        val cykController: CYKController,
                        val parseTreeController: ParseTreeController) {
    abstract fun apply()
}