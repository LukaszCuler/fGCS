package pl.lukasz.culer.settings

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import pl.lukasz.culer.annotations.Exclude
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarMeasure
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarMeasureFactory
import pl.lukasz.culer.fuzzy.memberships.SubtreeMembershipT2
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessorFactory
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessor
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessorFactory
import pl.lukasz.culer.fuzzy.snorms.SNormT2
import pl.lukasz.culer.fuzzy.tnorms.TNormT2
import pl.lukasz.culer.utils.Consts.Companion.DEFAULT_THRESHOLD
import pl.lukasz.culer.utils.JsonController
import pl.lukasz.culer.utils.Logger
import pl.lukasz.culer.utils.SETTINGS_LOADING
import java.io.File

const val TAG = "Settings"

class Settings {

    companion object {
        fun loadFromObject(filename : String?) : Settings {
            Logger.instance.d(TAG, SETTINGS_LOADING)
            return JsonController.gson.fromJson<Settings>(File(filename).readText(), Settings::class.java).apply { initialize() }
        }
    }
    //public methods
    fun initialize(){
        heatmapProcessor = heatmapProcessorFactory()
        relevanceProcessor = relevanceProcessorFactory()
        grammarMeasure = grammarMeasureFactory()
    }

    //initialization result - should not be parsed
    @Exclude
    lateinit var heatmapProcessor : HeatmapProcessor

    @Exclude
    lateinit var relevanceProcessor: RelevanceProcessor

    @Exclude
    lateinit var grammarMeasure: GrammarMeasure

    //"raw" setting parameters
    @SerializedName("sNorm")
    var sNorm = SNormT2.MAX

    @SerializedName("tNorm")
    var tNorm = TNormT2.MIN

    @SerializedName("subtreeMembership")
    var subtreeMembership = SubtreeMembershipT2.MIN_SQRT

    @SerializedName("threshold")
    var crispClassificationThreshold : Double? = DEFAULT_THRESHOLD      //if null it will be determined dynamically [recommended]

    //initializable parameters
    @SerializedName("heatmapProcessorFactory")
    var heatmapProcessorFactory : HeatmapProcessorFactory = HeatmapProcessorFactory.EQUAL_TREES

    @SerializedName("relevanceProcessorFactory")
    var relevanceProcessorFactory : RelevanceProcessorFactory = RelevanceProcessorFactory.WTA

    @SerializedName("grammarMeasureFactory")
    var grammarMeasureFactory : GrammarMeasureFactory = GrammarMeasureFactory.ENTROPY

    init {
        initialize()
    }
}