package pl.lukasz.culer.utils

import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers

class RxUtils {
    companion object {
        fun <T, N>computeParallelly(objsToCompute : List<T>, compFunc : (T) -> N) : List<N> {
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
    }
}