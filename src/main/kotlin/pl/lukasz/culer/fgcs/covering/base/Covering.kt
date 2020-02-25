package pl.lukasz.culer.fgcs.covering.base

import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.CYKTable

abstract class Covering {
    abstract fun apply(table : CYKTable,
              grammarController : GrammarController,
              cykController: CYKController)
}