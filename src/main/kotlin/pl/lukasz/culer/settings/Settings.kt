package pl.lukasz.culer.settings

import com.google.gson.annotations.SerializedName
import pl.lukasz.culer.annotations.Exclude
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.covering.base.Covering
import pl.lukasz.culer.fgcs.covering.base.CoveringFactory
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarPerfectionMeasure
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
        grammarPerfectionMeasure = grammarMeasureFactory()
    }

    //initialization result - should not be parsed
    @Exclude
    lateinit var heatmapProcessor : HeatmapProcessor

    @Exclude
    lateinit var relevanceProcessor: RelevanceProcessor

    @Exclude
    lateinit var grammarPerfectionMeasure: GrammarPerfectionMeasure

    //"raw" setting parameters
    @SerializedName("sOperatorReg")
    var sOperatorReg = SNormT2.MAX

    @SerializedName("tOperatorRev")
    var tOperatorRev = TNormT2.MIN

    @SerializedName("tOperatorReg")
    var tOperatorReg = SubtreeMembershipT2.MIN_SQRT

    @SerializedName("threshold")
    var crispClassificationThreshold : Double? = DEFAULT_THRESHOLD      //if null it will be determined dynamically [recommended]

    //initializable parameters
    @SerializedName("heatmapProcessorFactory")
    var heatmapProcessorFactory : HeatmapProcessorFactory = HeatmapProcessorFactory.EQUAL_TREES

    @SerializedName("relevanceProcessorFactory")
    var relevanceProcessorFactory : RelevanceProcessorFactory = RelevanceProcessorFactory.WTA

    @SerializedName("grammarMeasureFactory")
    var grammarMeasureFactory : GrammarMeasureFactory = GrammarMeasureFactory.CRISP_FITNESS

    @SerializedName("coveringFactory")
    var coveringFactory : CoveringFactory = CoveringFactory.COMPLETING

    @SerializedName("newSymbolSelectionMethod")
    var newSymbolSelectionMethod : GrammarController.NewSymbolSelectionMethod = GrammarController.NewSymbolSelectionMethod.INCREMENTAL

    @SerializedName("newSymbolCoef")
    var newSymbolCoef : Double = 1.0

    init {
        initialize()
    }
}