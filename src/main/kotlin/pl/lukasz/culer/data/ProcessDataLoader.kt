package pl.lukasz.culer.data

import com.google.gson.Gson
import pl.lukasz.culer.fgcs.models.Grammar
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class ProcessDataLoader {
    companion object {
        val gson = Gson()

        fun loadGrammar(filePath : String) : Grammar {
            val grammarString = BufferedReader(FileReader(filePath)).use { it.readText() }
            return gson.fromJson<Grammar>(grammarString, Grammar::class.java)
        }

        fun saveGrammar(grammar: Grammar, filePath: String){
            File(filePath).writeText(gson.toJson(grammar))
        }
    }
}