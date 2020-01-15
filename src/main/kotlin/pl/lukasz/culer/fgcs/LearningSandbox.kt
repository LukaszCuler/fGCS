package pl.lukasz.culer.fgcs

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import pl.lukasz.culer.data.AbbadingoLoader
import pl.lukasz.culer.data.ProcessDataLoader
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.*
import java.util.concurrent.TimeUnit

const val TAG = "LearningSandbox"

class LearningSandbox(private val params : InputParams) {
    var inputSet : List<TestExample>? = null
    var inputGrammar : Grammar? = null
    var testSet : List<TestExample>? = null
    var settings : Settings = Settings()

    fun startSimulation(){
        //preparing simulation...
        Logger.instance.d(TAG, LEARNING_SANDBOX_PREPARING_SIMULATION)

        params.inputSet?.let {inputSet = AbbadingoLoader.loadAbbadingoTestSet(it)}
        params.testSet?.let { testSet = AbbadingoLoader.loadAbbadingoTestSet(it)}
        params.grammarFile?.let { inputGrammar = ProcessDataLoader.loadGrammar(it)}
        params.settingsFile?.let {settings = Settings.loadFromObject(it)}
        //simulation
        Logger.instance.d(TAG, LEARNING_SANDBOX_LAUNCHING_SIMULATION)
        val fgcs = FGCS(inputSet, inputGrammar, testSet, params.maxIterations, settings)

        var simulationObservable = Observable.create<Boolean> {
            //this is the part where it learns
            fgcs.inferGrammar()
            it.onNext(false)
        }

        //adding timeout, if needed
        if(params.timeout!=null)
            simulationObservable = simulationObservable.timeout(params.timeout?.toLong() ?: 0L, TimeUnit.SECONDS, Observable.just(true))

        //running stuff!
        var simulation : Disposable? = null
        simulationObservable.subscribe(object : Observer<Boolean> {
            override fun onComplete() {
            }

            override fun onSubscribe(disposable: Disposable) {
                simulation = disposable
            }

            override fun onNext(timeout: Boolean) {
                if(timeout) Logger.instance.e(TAG, LEARNING_SANDBOX_SIMULATION_TIMEOUT)
                else fgcs.verifyPerformance()
                if(simulation?.isDisposed == false) simulation?.dispose()
            }

            override fun onError(e: Throwable) {
                Logger.instance.e(TAG, LEARNING_SANDBOX_SIMULATION_UNKNOWN_ERROR)
                e.printStackTrace()
            }
        })
    }
}

data class InputParams(var inputSet : String? = null,
                       var grammarFile : String? = null,
                       var testSet : String? = null,
                       var outputDict : String = "",
                       var settingsFile : String? = null,
                       var paramPairs : MutableList<Pair<String, String>> = mutableListOf(),
                       var timeout : Int? = null,
                       var maxIterations : Int? = null)