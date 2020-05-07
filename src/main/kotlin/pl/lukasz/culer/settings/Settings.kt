package pl.lukasz.culer.settings

import com.google.gson.annotations.SerializedName
import pl.lukasz.culer.annotations.Exclude
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.covering.base.Covering
import pl.lukasz.culer.fgcs.covering.base.CoveringFactory
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarPerfectionMeasure
import pl.lukasz.culer.fgcs.measures.grammar.base.GrammarMeasureFactory
import pl.lukasz.culer.fgcs.rules.base.*
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessor
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessorFactory
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessor
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessorFactory
import pl.lukasz.culer.fuzzy.snorms.SNormT2
import pl.lukasz.culer.fuzzy.tnorms.TNormT2
import pl.lukasz.culer.utils.Consts.Companion.DEFAULT_THRESHOLD
import pl.lukasz.culer.utils.Consts.Companion.MEMBERSHIP_SHORT_FORMATTER
import pl.lukasz.culer.utils.JsonController
import pl.lukasz.culer.utils.Logger
import pl.lukasz.culer.utils.SETTINGS_LOADING
import pl.lukasz.culer.vis.report.base.ReportsSaver
import pl.lukasz.culer.vis.report.base.ReportsSaverFactory
import java.io.File

const val TAG = "Settings"

class Settings {

    companion object {
        fun loadFromObject(filename : String?) : Settings {
            Logger.i(TAG, SETTINGS_LOADING)
            return JsonController.gson.fromJson<Settings>(File(filename).readText(), Settings::class.java).apply { initialize() }
        }
    }
    //public methods
    fun initialize(){
        heatmapProcessor = heatmapProcessorFactory()
        relevanceProcessor = relevanceProcessorFactory()
        grammarPerfectionMeasure = grammarMeasureFactory()
        membershipAssigner = membershipAssignerFactory()
        witheringSelector = witheringSelectorFactory()
        grammarPostProcessor = grammarPostProcessorFactory()
    }

    //initialization result - should not be parsed
    @Exclude
    lateinit var heatmapProcessor : HeatmapProcessor

    @Exclude
    lateinit var relevanceProcessor: RelevanceProcessor

    @Exclude
    lateinit var grammarPerfectionMeasure: GrammarPerfectionMeasure

    @Exclude
    lateinit var membershipAssigner: MembershipAssigner

    @Exclude
    lateinit var witheringSelector: WitheringSelector

    @Exclude
    lateinit var grammarPostProcessor: GrammarPostProcessor

    //"raw" setting parameters
    @SerializedName("sOperatorReg")
    var sOperatorReg = SNormT2.MAX

    @SerializedName("tOperatorRev")
    var tOperatorRev = TNormT2.MIN

    @SerializedName("tOperatorReg")
    var tOperatorReg = TNormT2.MIN_SQRT

    @SerializedName("threshold")
    var crispClassificationThreshold : Double? = DEFAULT_THRESHOLD      //if null it will be determined dynamically [recommended]

    @SerializedName("newSymbolSelectionMethod")
    var newSymbolSelectionMethod : GrammarController.NewSymbolSelectionMethod = GrammarController.NewSymbolSelectionMethod.INCREMENTAL

    @SerializedName("newSymbolCoef")
    var newSymbolCoef : Double = 1.0

    @SerializedName("bottomWithering_maxNumber")
    var bottomWitheringMaxNumber = 1

    @SerializedName("bottomWithering_proportionalProbability")
    var bottomWitheringProportionalProbability = false

    //initializable parameters
    @SerializedName("heatmapProcessorFactory")
    var heatmapProcessorFactory : HeatmapProcessorFactory = HeatmapProcessorFactory.EQUAL_TREES

    @SerializedName("relevanceProcessorFactory")
    var relevanceProcessorFactory : RelevanceProcessorFactory = RelevanceProcessorFactory.WTA

    @SerializedName("grammarMeasureFactory")
    var grammarMeasureFactory : GrammarMeasureFactory = GrammarMeasureFactory.CRISP_FITNESS

    @SerializedName("membershipAssignerFactory")
    var membershipAssignerFactory : MembershipAssignerFactory = MembershipAssignerFactory.SIMPLE_OCCURRENCE

    @SerializedName("witheringSelectorFactory")
    var witheringSelectorFactory : WitheringSelectorFactory = WitheringSelectorFactory.BOTTOM_WITHERING

    @SerializedName("grammarPostProcessorFactory")
    var grammarPostProcessorFactory : GrammarPostProcessorFactory = GrammarPostProcessorFactory.GENETIC_ALGORITHMS

    @SerializedName("coveringFactory")
    var coveringFactory : CoveringFactory = CoveringFactory.COMPLETING

    @SerializedName("reportsSaverFactory")
    var reportsSaverFactory : ReportsSaverFactory = ReportsSaverFactory.TEXT_REPORT

    init {
        initialize()
    }

    override fun toString(): String = JsonController.gson.toJson(this)
}