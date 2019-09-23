package pl.lukasz.culer.ui

import pl.lukasz.culer.data.ProcessDataLoader
import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.InputParams
import pl.lukasz.culer.fgcs.LearningSandbox
import pl.lukasz.culer.fgcs.controllers.DataCollectionController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.rules.NRuleRHS
import pl.lukasz.culer.fgcs.models.rules.TRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.fgcs.models.symbols.TSymbol
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.LINE_LAUNCHER_LOADED
import pl.lukasz.culer.utils.Logger


fun main(args: Array<String>) {
    LearningSandbox(
        LineLauncher(args)
            .getInputParams()
    ).startSimulation()
    /*val grammar = Grammar()



    val tA = TSymbol('a')
    val tB = TSymbol('b')

    val nS = NSymbol(Consts.DEFAULT_START_SYMBOL, true)
    val nA = NSymbol('A')
    val nB = NSymbol('B')
    val nC = NSymbol('C')

    grammar.nSymbols.add(nS)
    grammar.nSymbols.add(nA)
    grammar.nSymbols.add(nB)
    grammar.nSymbols.add(nC)
    grammar.starSymbol = nS

    grammar.tSymbols.add(tA)
    grammar.tSymbols.add(tB)

    grammar.tRules.add(TRule(nA, tA))
    grammar.tRules.add(TRule(nB, tB))

    val grammarController = GrammarController(grammar)

    grammarController.addNRule(NRule(nS, NRuleRHS(nA, nB), IntervalFuzzyNumber(1.0)))
    grammarController.addNRule(NRule(nS, NRuleRHS(nA, nC), IntervalFuzzyNumber(1.0)))
    grammarController.addNRule(NRule(nC, NRuleRHS(nS, nB), IntervalFuzzyNumber(1.0)))
    grammarController.addNRule(NRule(nA, NRuleRHS(nS, nS), IntervalFuzzyNumber(0.2)))

    ProcessDataLoader.saveGrammar(grammar, "testgrammar.txt")*/
}

/**
 * Komendy dostępne z linii poleceń:
 *  -i - /opcjonalny/ zbiór uczący w formacie abbadingo, w przypadku braku nie jest przeprowadzany proces uczenia
 *  -g - /opcjonalny/ wykluczający się z -i - wczytuje gramatykę do badań
 *  -o - nazwa katalogu wyjściowego, do którego zapisywane są gramatyki, raporty i wyniki. [MOD] - zamieniane jest na datę + czas
 *  -t - /opcjonalny/ - zbiór testowy, w przypadku braku wykorzystywany jest uczący
 *  -s - /opcjonalny/ - plik zawierający ustawienia dla symulacji, w przypadku braku wykorzystywane są standardowe
 *  -p - /opcjonalny/ - podmiana wartości konkretnego parametru
 *  -tout - /opcjonalny/ - timeout w sekundach
 **/
const val INPUT = "i"
const val GRAMMAR = "g"
const val OUTPUT = "o"
const val TEST = "t"
const val SETTINGS = "s"
const val PARAMETER = "p"
const val TIMEOUT = "tout"

const val TAG = "LineLauncher"

class LineLauncher(private val args: Array<String>) {

    fun getInputParams() : InputParams {
        val commandsFromArgs = getCommandsFromArgs(args)
        val inputParams = InputParams()
        commandsFromArgs.forEach { dispatchCommand(inputParams, it) }
        Logger.instance.i(TAG, LINE_LAUNCHER_LOADED.format(inputParams.toString()))
        return inputParams
    }

    private fun getCommandsFromArgs(args: Array<String>): List<LineCommand> {
        val commands = mutableListOf<LineCommand>()

        var currentLineCommand: LineCommand? = null
        for (i in args.indices) {
            if (isCommand(args[i])) {
                //new command
                if (currentLineCommand != null) commands.add(currentLineCommand)
                currentLineCommand = LineCommand(argToCommand(args[i]))
            } else {
                //not command - terminating, if not previous rule
                if (currentLineCommand == null) return listOf()
                //or adding value
                currentLineCommand.values.add(args[i])
            }
        }

        //adding last command
        if (currentLineCommand!=null) commands.add(currentLineCommand)
        return commands
    }

    private fun dispatchCommand(inputParams: InputParams, lineCommand: LineCommand) {
        when (lineCommand.command) {
            INPUT -> handleInput(inputParams, lineCommand.values)
            GRAMMAR -> handleGrammar(inputParams, lineCommand.values)
            OUTPUT -> handleOutput(inputParams, lineCommand.values)
            TEST -> handleTest(inputParams, lineCommand.values)
            SETTINGS -> handleSettings(inputParams, lineCommand.values)
            PARAMETER -> handleParameter(inputParams, lineCommand.values)
            TIMEOUT -> handleTimeout(inputParams, lineCommand.values)
        }
    }

    private fun handleInput(inputParams: InputParams, file: List<String>) {
        if (file.isNotEmpty()) inputParams.inputSet = file[0]
    }

    private fun handleGrammar(inputParams: InputParams, grammar: List<String>) {
        if (grammar.isNotEmpty()) inputParams.grammarFile = grammar[0]
    }

    private fun handleTest(inputParams: InputParams, file: List<String>) {
        if (file.isNotEmpty()) inputParams.testSet = file[0]
    }

    private fun handleOutput(inputParams: InputParams, outputDict: List<String>) {
        if (outputDict.isNotEmpty()) inputParams.outputDict = outputDict[0]
    }

    private fun handleSettings(inputParams: InputParams, settingsFile: List<String>) {
        if (settingsFile.isNotEmpty()) inputParams.settingsFile = settingsFile[0]
    }

    private fun handleParameter(inputParams: InputParams, params: List<String>) {
        if (params.isNotEmpty() && params.size % 2 == 0){
            inputParams.paramPairs = params
                .groupBy { params.indexOf(it)/2 }
                .map { Pair(it.value[0], it.value[1]) }
                .toMutableList()
        }
    }

    private fun handleTimeout(inputParams: InputParams, timeout: List<String>)
    {
        if (timeout.isNotEmpty()) inputParams.timeout = timeout[0].toIntOrNull()
    }

    private fun isCommand(cmd: String) = cmd.length > 1 && cmd[0] == '-'

    private fun argToCommand(arg: String) = arg.subSequence(1, arg.length).toString()
}

private data class LineCommand(val command : String,
                               val values : MutableList<String> = mutableListOf())