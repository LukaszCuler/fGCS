package pl.lukasz.culer.vis.report

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.fgcs.models.reports.FinalResult
import pl.lukasz.culer.fgcs.models.reports.InitData
import pl.lukasz.culer.fgcs.models.reports.Iteration
import pl.lukasz.culer.fgcs.models.rules.NRule
import pl.lukasz.culer.fgcs.models.symbols.NSymbol
import pl.lukasz.culer.utils.Logger
import pl.lukasz.culer.utils.REPORTS_CONTROLLER_ITERATION_SAVED
import pl.lukasz.culer.utils.REPORTS_CONTROLLER_ITERATION_SAVE_ERROR
import pl.lukasz.culer.utils.REPORTS_CONTROLLER_START_INFERENCE
import pl.lukasz.culer.vis.report.base.ReportsSaver
import java.text.SimpleDateFormat
import java.util.*

/**
 * @TODO UT
 */
class ReportsController(private val reportsSaver: ReportsSaver) {
    companion object {
        const val TAG = "ReportsController"

        const val datePattern = "YYYYMMddHHmmss"
        const val reportNamePrefix = "Report-"
    }

    private lateinit var currentIteration : Iteration
    private lateinit var consumerSub : Disposable
    private var iterationConsumer : ReplaySubject<Iteration> = ReplaySubject.create()

    init {
        reportsSaver.initialize("$reportNamePrefix${SimpleDateFormat(datePattern).format(Date())}")
        iterationConsumer
            .subscribeOn(Schedulers.single())
            .subscribe(object : Observer<Iteration> {
                override fun onComplete() {
                }

                override fun onSubscribe(sub: Disposable) {
                    consumerSub = sub
                }

                override fun onNext(iterationToSave: Iteration) {
                    reportsSaver.saveIteration(iterationToSave)
                    Logger.d(TAG, REPORTS_CONTROLLER_ITERATION_SAVED.format(iterationToSave.iterationNum))
                }

                override fun onError(error: Throwable) {
                    Logger.e(TAG, REPORTS_CONTROLLER_ITERATION_SAVE_ERROR)
                    error.printStackTrace()
                }

            })
    }

    fun startInference(initData: InitData){
        Logger.e(TAG, REPORTS_CONTROLLER_START_INFERENCE)
        reportsSaver.saveInferenceInitialData(initData)
    }

    fun finishInference(finalResult: FinalResult){
        iterationConsumer.onComplete()
        iterationConsumer.blockingLast()        //waiting to complete saving previous
        reportsSaver.saveInferenceFinalData(finalResult)

    }

    fun startIteration(itNum : Int){
        currentIteration = Iteration(itNum)
    }

    fun finishIteration(grammar : Grammar, analizedExamples : List<FGCS.ExampleAnalysisResult>, perfectionMeasure : Double){
        currentIteration.grammar = grammar.copy()
        currentIteration.analizedExamples = analizedExamples        //no need to copy -> created per iteration
        currentIteration.perfectionMeasure = perfectionMeasure
        iterationConsumer.onNext(currentIteration)
    }

    fun addedRule(addedRule : NRule, source : String){
        currentIteration.addedRules.add(addedRule to source)
    }

    fun removedRule(removedRule : NRule, source : String){
        currentIteration.removedRules.add(removedRule to source)
    }

    fun addedSymbol(addedSymbol : NSymbol, source : String){
        currentIteration.addedSymbols.add(addedSymbol to source)
    }

    fun removedSymbol(removedSymbol : NSymbol, source : String){
        currentIteration.removedSymbols.add(removedSymbol to source)
    }

    fun finishVerification(testExamples : List<FGCS.ExampleAnalysisResult>){
        reportsSaver.saveTestResults(testExamples)
        reportsSaver.finalize()
    }
}