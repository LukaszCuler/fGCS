import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeNode {

    String name;
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
        buffer.append(prefix);
        buffer.append(name);
        buffer.append("<br>");
        for (Iterator<TreeNode> it = children.iterator(); it.hasNext();) {
            TreeNode next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├──&nbsp", childrenPrefix + "│&nbsp&nbsp&nbsp");
            } else {
                next.print(buffer, childrenPrefix + "└──&nbsp", childrenPrefix + "&nbsp&nbsp&nbsp&nbsp");
            }
        }
    }
}
