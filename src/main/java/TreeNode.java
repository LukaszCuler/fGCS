import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeNode {

    String name;
    public double memb;
    public List<TreeNode> children = new ArrayList<>();

    public TreeNode(String name) {
        this.name = name;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        int colorR = (int)(255*(1.0-memb));
        int colorG = (int)(255*memb);
        String hexR = String.format("%02x",colorR);
        String hexG = String.format("%02x",colorG);

        String colorCommand = "<font color='#"+hexR+""+hexG+"00'>";
        String endColor = "</font>";
        buffer.append(prefix);
        buffer.append(colorCommand+name+endColor);
        buffer.append("<br>");
        for (Iterator<TreeNode> it = children.iterator(); it.hasNext();) {
            TreeNode next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + colorCommand + "├──&nbsp"+endColor, childrenPrefix + colorCommand + "│&nbsp&nbsp&nbsp"+endColor);
            } else {
                next.print(buffer, childrenPrefix + colorCommand + "└──&nbsp"+endColor, childrenPrefix + colorCommand + "&nbsp&nbsp&nbsp&nbsp"+endColor);
            }
        }
    }
}
