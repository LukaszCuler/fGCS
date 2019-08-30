package pl.lukasz.culer.ui

import pl.lukasz.culer.fgcs.InputParams
import pl.lukasz.culer.utils.LINE_LAUNCHER_LOADED
import pl.lukasz.culer.utils.Logger


fun main(args: Array<String>) {
    println("Hello World!")
}

/**
 * Komendy dostępne z linii poleceń:
 *  -i - zbiór uczący w formacie abbadingo
 *  -o - nazwa katalogu wyjściowego, do którego zapisywane są gramatyki, raporty i wyniki. [MOD] - zamieniane jest na datę + czas
 *  -t - /opcjonalny/ - zbiór testowy, w przypadku braku wykorzystywany jest uczący
 *  -s - /opcjonalny/ - plik zawierający ustawienia dla symulacji, w przypadku braku wykorzystywane są standardowe
 *  -p - /opcjonalny/ - podmiana wartości konkretnego parametru
 *  -tout - /opcjonalny/ - timeout w sekundach
 **/
const val INPUT = "i"
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