package pl.lukasz.culer.settings

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import pl.lukasz.culer.fuzzy.memberships.SubtreeMembershipT2
import pl.lukasz.culer.fuzzy.processors.heatmap.base.HeatmapProcessorFactory
import pl.lukasz.culer.fuzzy.processors.relevance.base.RelevanceProcessorFactory
import pl.lukasz.culer.fuzzy.snorms.SNormT2
import pl.lukasz.culer.fuzzy.tnorms.TNormT2
import pl.lukasz.culer.utils.Logger
import pl.lukasz.culer.utils.SETTINGS_LOADING
import java.io.File

const val TAG = "Settings"

class Settings {

    companion object {
        fun loadFromObject(filename : String?) : Settings {
            Logger.instance.d(TAG, SETTINGS_LOADING)
            return Gson().fromJson<Settings>(File(filename).readText(), Settings::class.java)
        }
    }

    @SerializedName("sNorm")
    val sNorm = SNormT2.MAX

    @SerializedName("tNorm")
    val tNorm = TNormT2.MIN

    @SerializedName("subtreeMembership")
    val subtreeMembership = SubtreeMembershipT2.MIN_SQRT

    @SerializedName("heatmapProcessorFactory")
    val heatmapProcessorFactory : HeatmapProcessorFactory = HeatmapProcessorFactory.MINMAX

    @SerializedName("relevanceProcessorFactory")
    val relevanceProcessorFactory : RelevanceProcessorFactory = RelevanceProcessorFactory.WTA

    @SerializedName("threshold")
    val crispClassificationThreshold : Double? = 0.5       //if null it will be determined dynamically [recommended]
}