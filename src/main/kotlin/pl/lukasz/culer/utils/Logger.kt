package pl.lukasz.culer.utils

object Logger {
    const val LOG_DEBUG = 0
    const val LOG_INFO = 1
    const val LOG_ERROR = 2

    const val LVL_NAME_DEBUG = "d"
    const val LVL_NAME_INFO = "i"
    const val LVL_NAME_ERROR = "e"

    val LVL_TO_NAME_MAP = mapOf(
        LOG_DEBUG to LVL_NAME_DEBUG,
        LOG_INFO to LVL_NAME_INFO,
        LOG_ERROR to LVL_NAME_ERROR)

    const val MUTE = 1000

    var currentLogLevel : Int = LOG_DEBUG

    fun d(tag : String, message : String) = log(LOG_DEBUG, tag, message)

    fun i(tag : String, message : String) = log(LOG_INFO, tag, message)

    fun e(tag : String, message : String) = log(LOG_ERROR, tag, message)

    fun mute() {
        currentLogLevel = MUTE
    }

    private fun log(level : Int, tag : String, message : String){
        if(currentLogLevel<=level) println("[${LVL_TO_NAME_MAP[level]}][$tag] $message")
    }
}