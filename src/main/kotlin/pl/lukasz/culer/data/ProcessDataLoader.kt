package pl.lukasz.culer.data

import com.google.gson.Gson
import pl.lukasz.culer.fgcs.models.Grammar
import pl.lukasz.culer.utils.JsonController
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class ProcessDataLoader {
    companion object {
        fun loadGrammar(filePath : String) : Grammar {
            val grammarString = BufferedReader(FileReader(filePath)).use { it.readText() }
            return JsonController.gson.fromJson<Grammar>(grammarString, Grammar::class.java)
        }

        fun saveGrammar(grammar: Grammar, filePath: String){
            File(filePath).writeText(JsonController.gson.toJson(grammar))
        }
    }
}