package pl.lukasz.culer.utils

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers

class RxUtils {
    companion object {
        fun <T, N>computeParallelly(objsToCompute : List<T>, compFunc : (T) -> N) : List<N> {
            if(objsToCompute.isEmpty()) return listOf()
            return Single.zip(objsToCompute.map { obj ->
                Single.create(SingleOnSubscribe<N> {
                    try {
                        it.onSuccess(compFunc(obj))
                    } catch (ex : Throwable) {
                        it.onError(ex)
                    }

                }).doOnError {
                    it.printStackTrace()
                }
            }.toList()) { resultsArray ->
                resultsArray.map { it as N }.toList()
            }.subscribeOn(Schedulers.computation()).blockingGet()
        }

        fun <T, N>computeParallelly(objs : Observable<T>, compFunc : (T) -> N) : List<N> {
            return objs.flatMap { obj ->
                Observable.create(ObservableOnSubscribe<N> {
                    try {
                        it.onNext(compFunc(obj))
                    } catch (ex: Throwable) {
                        it.onError(ex)
                    }
                    it.onComplete()
                }).doOnError {
                    it.printStackTrace()
                }
            }.subscribeOn(Schedulers.computation())
                .blockingIterable()
                .toList()
        }
    }
}