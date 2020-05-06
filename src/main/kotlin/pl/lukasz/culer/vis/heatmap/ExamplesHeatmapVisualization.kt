package pl.lukasz.culer.vis.heatmap

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.controllers.ClassificationController
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import pl.lukasz.culer.fuzzy.IntervalFuzzyNumber
import pl.lukasz.culer.settings.Settings
import pl.lukasz.culer.utils.Consts
import pl.lukasz.culer.utils.Consts.Companion.FULL_MEMBERSHIP
import pl.lukasz.culer.utils.Consts.Companion.MEMBERSHIP_SHORT_FORMATTER
import pl.lukasz.culer.utils.TextReport
import java.io.File

const val HEAD_CONTENT_FILE = "fgcsUtils/reports/start_heatmap.html"
const val TAIL_CONTENT_FILE = "fgcsUtils/reports/end_heatmap.html"
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
const val SUPER_ROOT = "#"

class ExamplesHeatmapVisualization(val grammarController : GrammarController,
                                   val classificationController : ClassificationController,
                                   val settings: Settings,
                                   val listToVisualize : List<FGCS.ExampleAnalysisResult>) : TextReport(){
    /**
     * region public methods
     */
    fun saveToFile(filePath : String){
        var html = getTemplate(HEAD_CONTENT_FILE)  //starts html file with proper head

        for(example in listToVisualize){
            val fuzzyClass = example.multiParseTreeNode.classificationMembership
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

            if(!example.multiParseTreeNode.isDeadEnd &&
                (settings.heatmapProcessor.mainTreeDistinguishable()
                        || settings.heatmapProcessor.showAllSubtrees())){
                val startNodes = getNode(example.multiParseTreeNode)
                val root : TreeNode
                if(startNodes.size==1){
                    root = startNodes.first()
                } else {
                    root = TreeNode(SUPER_ROOT)
                    root.membership = FULL_MEMBERSHIP.midpoint
                    root.children.addAll(startNodes)
                }
                html+= PARSE_TREE_CONTAINER_START+root+PARSE_TREE_CONTAINER_END
            }
        }

        html+= getTemplate(TAIL_CONTENT_FILE)        //adds proper tail
        initReport(filePath, html)
    }
    //endregion

    /**
     * region private methods
     */
    private fun getNode(tree : MultiParseTreeNode, inhValue : IntervalFuzzyNumber = Consts.FULL_MEMBERSHIP) : List<TreeNode> {
        val nodesList = mutableListOf<TreeNode>()
        if(tree.isLeaf){
            val myNode = TreeNode(tree.node.symbol.toString())
            //leaf time!
            myNode.membership = inhValue.midpoint
            myNode.children.add(TreeNode(grammarController.tRulesWith(left = tree.node).single().getRight().symbol.toString()).apply { membership = inhValue.midpoint })
            nodesList.add(myNode)
        } else {
            //we have to print children
            if(settings.heatmapProcessor.mainTreeDistinguishable()){
                val myNode = TreeNode(tree.node.symbol.toString())
                val mainTree = settings.heatmapProcessor.getMainTree(tree, grammarController) ?: return mutableListOf(myNode)
                myNode.membership = mainTree.derivationMembership.midpoint
                myNode.children.addAll(getNode(mainTree.subTreePair.first, mainTree.derivationMembership))
                myNode.children.addAll(getNode(mainTree.subTreePair.second, mainTree.derivationMembership))
                nodesList.add(myNode)
            } else {
                for(idx in tree.subtrees.indices){
                    val myNode = TreeNode("${tree.node.symbol} <sub>${idx+1}</sub>")
                    myNode.membership = tree.subtrees[idx].derivationMembership.midpoint
                    myNode.children.addAll(getNode(tree.subtrees[idx].subTreePair.first, tree.subtrees[idx].derivationMembership))
                    myNode.children.addAll(getNode(tree.subtrees[idx].subTreePair.second, tree.subtrees[idx].derivationMembership))
                    nodesList.add(myNode)
                }
            }
        }
        return nodesList
    }
    //endregion
}