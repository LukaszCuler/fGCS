package pl.lukasz.culer.utils

//@TODO add logging
class Logger {
    companion object {
        val instance = Logger()

        const val LOG_DEBUG = 0
        const val LOG_INFO = 1
        const val LOG_ERROR = 2
        const val MUTE = 1000
    }

    var currentLogLevel : Int = LOG_DEBUG

    fun d(tag : String, message : String) = log(LOG_DEBUG, tag, message)

    fun i(tag : String, message : String) = log(LOG_INFO, tag, message)

    fun e(tag : String, message : String) = log(LOG_ERROR, tag, message)

    fun mute() {
        currentLogLevel = MUTE
    }

    private fun log(level : Int, tag : String, message : String){
        if(currentLogLevel<=level) println("[$tag] $message")
    }
}