package pl.lukasz.culer.fgcs

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import pl.lukasz.culer.data.AbbadingoLoader
import pl.lukasz.culer.data.TestExample
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.*
import java.util.concurrent.TimeUnit

const val TAG = "LearningSandbox"

class LearningSandbox(private val params : InputParams) {
    lateinit var inputSet : List<TestExample>
    var testSet : List<TestExample>? = null
    var settings : Settings = Settings()

    fun startSimulation(){
        //preparing simulation...
        Logger.instance.d(TAG, LEARNING_SANDBOX_PREPARING_SIMULATION)

        inputSet = AbbadingoLoader.loadAbbadingoTestSet(params.inputSet)
        if(params.testSet!=null) testSet = AbbadingoLoader.loadAbbadingoTestSet(params.inputSet)
        if(params.settingsFile!=null) settings = Settings.loadFromObject(params.settingsFile)
        //simulation
        Logger.instance.d(TAG, LEARNING_SANDBOX_LAUNCHING_SIMULATION)

        var simulationObservable = Observable.create<Boolean> {
            //this is the part where it learns
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
                else verify()
                if(simulation?.isDisposed == false) simulation?.dispose()
            }

            override fun onError(e: Throwable) {
                Logger.instance.e(TAG, LEARNING_SANDBOX_SIMULATION_UNKNOWN_ERROR)
                e.printStackTrace()
            }
        })
    }

    fun verify(){
        //verification time!
        val set = testSet ?: inputSet

    }
}

data class InputParams(var inputSet : String = "",
                       var testSet : String? = null,
                       var outputDict : String = "",
                       var settingsFile : String? = null,
                       var paramPairs : MutableList<Pair<String, String>> = mutableListOf(),
                       var timeout : Int? = null)