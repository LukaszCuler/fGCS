package pl.lukasz.culer.fgcs.covering.base

import pl.lukasz.culer.fgcs.controllers.CYKController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.controllers.ParseTreeController
import pl.lukasz.culer.fgcs.covering.CompletingCovering
import pl.lukasz.culer.fgcs.measures.grammar.EntropyGrammarPerfectionMeasure
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarPerfectionMeasure
import pl.lukasz.culer.fgcs.models.CYKTable

enum class CoveringFactory : (CYKTable, GrammarController, CYKController, ParseTreeController) -> Covering {
    COMPLETING {
        override fun invoke(table: CYKTable,
                            grammarController: GrammarController,
                            cykController: CYKController,
                            parseTreeController: ParseTreeController): Covering = CompletingCovering(table, grammarController, cykController, parseTreeController)
    }
}