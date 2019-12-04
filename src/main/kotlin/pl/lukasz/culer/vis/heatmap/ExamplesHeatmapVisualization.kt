package pl.lukasz.culer.vis.heatmap

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.controllers.ClassificationController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.MEMBERSHIP_SHORT_FORMATTER
import java.io.File

const val HEAD_CONTENT_FILE = "fgcsUtils/reports/start.html"
const val TAIL_CONTENT_FILE = "fgcsUtils/reports/end.html"
const val POSITIVE_LABEL = "<span class=\"label label-success\">positive</span>"
const val NEGATIVE_LABEL = "<span class=\"label label-danger\">negative</span>"
const val SEQUENCE_INIT = "&nbsp&nbsp&nbsp&nbsp<span class=\"lead\">"
const val EXAMPLE_NOT_PARSED = "<font color='#ff0000'>"
const val EXAMPLE_END = "</font>"
const val HEX_FORMATTER = "%02x"
const val MAX_COLOR_VALUE = 255
const val CUSTOM_COLOR_START = "<font color='#"
const val CUSTOM_COLOR_END = "00'>"
const val MEMBERSHIP_VALUE_START = "&nbsp&nbsp&nbsp&nbsp<span class=\"label label-primary\">"
const val END_ALL_FORMATTING = "</span></span>"
const val NEW_LINE = "<br>"
const val PARSE_TREE_CONTAINER_START = "<tt>"
const val PARSE_TREE_CONTAINER_END = "</tt><br><br>"

class ExamplesHeatmapVisualization(val grammarController : GrammarController,
                                   val classificationController : ClassificationController,
                                   val listToVisualize : List<FGCS.ExampleAnalysisResult>){
    /**
     * region public methods
     */
    fun saveToFile(filePath : String){
        var html = File(HEAD_CONTENT_FILE).readText()   //starts html file with proper head

        for(example in listToVisualize){
            val fuzzyClass = classificationController.getFuzzyClassification(example.multiParseTreeNode)
            val crispClass = classificationController.getCrispClassification(example.multiParseTreeNode)
            val exampleHeatmap = classificationController.getExampleHeatmap(example.multiParseTreeNode)

            var exampleString = if(crispClass) POSITIVE_LABEL
            else NEGATIVE_LABEL
            exampleString += SEQUENCE_INIT
            if(example.multiParseTreeNode.isDeadEnd){
                exampleString += EXAMPLE_NOT_PARSED+example.example.sequence+EXAMPLE_END
            } else {
                for(i in 0 until example.example.size){
                    val colorR = (MAX_COLOR_VALUE*(1.0-exampleHeatmap[i].midpoint)).toInt()
                    val colorG = (MAX_COLOR_VALUE*exampleHeatmap[i].midpoint).toInt()
                    val hexR = HEX_FORMATTER.format(colorR)
                    val hexG = HEX_FORMATTER.format(colorG)

                    exampleString += CUSTOM_COLOR_START + hexR + hexG + CUSTOM_COLOR_END+example.example.sequence[i]+EXAMPLE_END
                }
            }
            exampleString+=MEMBERSHIP_VALUE_START+MEMBERSHIP_SHORT_FORMATTER.format(fuzzyClass.midpoint)+END_ALL_FORMATTING
            html += exampleString+NEW_LINE

            if(!example.multiParseTreeNode.isDeadEnd && classificationController.heatmapProcessor.mainTreeDistinguishable()){
                val root = getNode(example.multiParseTreeNode)
                html+= PARSE_TREE_CONTAINER_START+root+PARSE_TREE_CONTAINER_END
            }
        }

        html+=File(TAIL_CONTENT_FILE).readText()        //adds proper tail
        File(filePath).writeText(html)
    }
    //endregion

    /**
     * region private methods
     */
    private fun getNode(tree : MultiParseTreeNode, inhValue : IntervalFuzzyNumber = Consts.FULL_MEMBERSHIP) : TreeNode {
        val myNode = TreeNode(tree.node.symbol.toString())
        if(tree.isLeaf){
            myNode.membership = inhValue.midpoint
            myNode.children.add(TreeNode(grammarController.tRulesWith(left = tree.node).single().getRight().symbol.toString()).apply { membership = inhValue.midpoint })
        } else {
            val mainTree = classificationController.heatmapProcessor.getMainTree(tree.subtrees) ?: return myNode
            myNode.membership = mainTree.derivationMembership.midpoint
            myNode.children.add(getNode(mainTree.subTreePair.first, mainTree.derivationMembership))
            myNode.children.add(getNode(mainTree.subTreePair.second, mainTree.derivationMembership))
        }
        return myNode
    }
    //endregion
}