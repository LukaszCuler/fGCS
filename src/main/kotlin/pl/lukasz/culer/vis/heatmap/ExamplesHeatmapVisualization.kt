package pl.lukasz.culer.vis.heatmap

import pl.lukasz.culer.fgcs.FGCS
import pl.lukasz.culer.fgcs.controllers.GrammarController
import pl.lukasz.culer.fgcs.models.trees.MultiParseTreeNode
import java.io.File

const val HEAD_CONTENT_FILE = "fgcs-utils/reports/start.html"
const val TAIL_CONTENT_FILE = "fgcs-utils/reports/end.html"

class ExamplesHeatmapVisualization(val grammarController : GrammarController, val listToVisualize : List<FGCS.ExampleAnalysisResult>){
    /**
     * region public methods
     */
    fun performAnalysis() {

    }

    fun saveToFile(filePath : String){
        var html = File(HEAD_CONTENT_FILE).readText()   //starts html file with proper head

        html+=File(TAIL_CONTENT_FILE).readText()        //adds proper tail
        File(filePath).writeText(html)
    }
    //endregion

    /**
     * region private methods
     */
    private fun getNode(tree : MultiParseTreeNode) : TreeNode {
        val myNode = TreeNode(tree.node.symbol.toString())
        myNode.membership = tree.mainMembership.midpoint;
        if(tree.isLeaf){
            myNode.children.add(TreeNode(grammarController.tRulesWith(left = tree.node).single().getRight().symbol.toString()).apply { membership = tree.mainMembership.midpoint })
        } else {
            myNode.children.add(getNode(tree.mainChild!!.subtrees.first))
            myNode.children.add(getNode(tree.mainChild!!.subtrees.second))
        }
        return myNode
    }
    //endregion
}