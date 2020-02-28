package pl.lukasz.culer.fgcs.covering

import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.covering.base.Covering
import pl.lukasz.culer.fgcs.models.CYKTable

class CompletingCovering : Covering() {
    override fun apply(table: CYKTable, grammarController: GrammarController, cykController: CYKController) {
        //filling with potential rules
    }
}