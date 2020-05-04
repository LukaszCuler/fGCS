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
import pl.lukasz.culer.vis.report.base.ReportsSaver
import java.text.SimpleDateFormat
import java.util.*

/**
 * @TODO UT
 */
class ReportsController(private val reportsSaver: ReportsSaver) {
    companion object {
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
                    //Log
                }

                override fun onError(error: Throwable) {
                    //Log
                }

            })
    }

    fun startReport(initData: InitData){

    }

    fun startIteration(itNum : Int){
        currentIteration = Iteration(itNum)
    }

    fun addedRule(rule : NRule, source : String){

    }

    fun removedRule(removedRule : NRule, source : String){

    }

    fun finishIteration(grammar : Grammar, analizedExamples : List<FGCS.ExampleAnalysisResult>, perfectionMeasure : Double){
        currentIteration.grammar = grammar.copy()
        currentIteration.analizedExamples = analizedExamples        //no need to copy -> created per iteration
        currentIteration.perfectionMeasure = perfectionMeasure
        iterationConsumer.onNext(currentIteration)
    }

    fun finishReport(finalResult: FinalResult){
        iterationConsumer.onComplete()
        iterationConsumer.blockingLast()        //waiting to complete saving previous
        reportsSaver.saveFinalData(finalResult)
        reportsSaver.finalize()
    }
}