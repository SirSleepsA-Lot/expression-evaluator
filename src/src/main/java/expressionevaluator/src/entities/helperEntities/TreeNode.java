package expressionevaluator.src.entities.helperEntities;

public class TreeNode {
    Object value;
    TreeNode left, right;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public TreeNode getLeft() {
        return left;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public TreeNode getRight() {
        return right;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }

    public TreeNode(Object value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}
