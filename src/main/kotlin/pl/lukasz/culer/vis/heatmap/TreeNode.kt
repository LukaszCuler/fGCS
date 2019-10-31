package pl.lukasz.culer.vis.heatmap

import java.util.ArrayList

const val EMPTY_STING = ""
const val UNIT_VAL = 1.0
const val HEX_COLOR_FORMATTER = "%02x"
const val END_COLOR = "</font>"
const val N_TH_ELEMENT_PREFIX = "├──&nbsp"
const val N_TH_ELEMENT_POSTFIX = "│&nbsp&nbsp&nbsp"
const val LAST_ELEMENT_PREFIX = "└──&nbsp"
const val LAST_ELEMENT_POSTFIX = "&nbsp&nbsp&nbsp&nbsp"

class TreeNode(private val nodeName : String, var membership : Double = 0.0, val children: MutableList<TreeNode> = mutableListOf()) {
    override fun toString(): String {
        val buffer = StringBuilder()
        print(buffer, EMPTY_STING, EMPTY_STING)
        return buffer.toString()
    }

    private fun print(buffer: StringBuilder, prefix: String, childrenPrefix: String) {
        //data preparation
        val colorR = (MAX_COLOR_VALUE * (UNIT_VAL - membership)).toInt()
        val colorG = (MAX_COLOR_VALUE * membership).toInt()
        val hexR = String.format(HEX_COLOR_FORMATTER, colorR)
        val hexG = String.format(HEX_COLOR_FORMATTER, colorG)
        val colorCommand = "<font color='#$hexR${hexG}00'>"

        //building node
        buffer.append(prefix)
        buffer.append(colorCommand)
        buffer.append(nodeName)
        buffer.append(END_COLOR)
        buffer.append(NEW_LINE)

        //attaching children
        val it = children.iterator()
        while (it.hasNext()) {
            val next = it.next()
            if (it.hasNext()) {
                next.print(
                    buffer, "$childrenPrefix$colorCommand$N_TH_ELEMENT_PREFIX$END_COLOR",
                    "$childrenPrefix$colorCommand$N_TH_ELEMENT_POSTFIX$END_COLOR"
                )
            } else {
                next.print(
                    buffer, "$childrenPrefix$colorCommand$LAST_ELEMENT_PREFIX$END_COLOR",
                    "$childrenPrefix$colorCommand$LAST_ELEMENT_POSTFIX$END_COLOR"
                )
            }
        }
    }
}