package pl.lukasz.culer.utils

import pl.lukasz.culer.vis.heatmap.TAIL_CONTENT_FILE
import java.io.File

open class TextReport {
    //region consts
    companion object {
        private const val REPORT_TEMPLATES_BASE = "fgcsUtils/reportTemplates/"
        private const val EMPTY_STRING = ""
    }
    //endregion
    //region properties
    private val loadedTemplates = mutableMapOf<String, String>()
    private var reportFile : File? = null
    //endregion
    //region public methods
    fun initReport(reportPath : String, initContent : String = EMPTY_STRING) {
        reportFile = File(reportPath)
        reportFile?.writeText(initContent)
    }

    fun addToReport(contentToAdd : String){
        reportFile?.appendText(contentToAdd)
    }

    fun getTemplate(template : String) : String {
        if(!loadedTemplates.containsKey(template)){
            loadedTemplates[template] = File(REPORT_TEMPLATES_BASE+template).readText()
        }
        return loadedTemplates[template] ?: EMPTY_STRING
    }
    //endregion
}