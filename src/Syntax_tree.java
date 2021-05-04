public class Syntax_tree {
  Syntax_tree_node root = null;
  Syntax_tree_node current = null;
  Syntax_tree next_tree = null;
  int program_numb;

  void add_node(String name){
    if (root == null){
      //make root
      root = new Syntax_tree_node(name);
      current = root;
    } else {
      //new node
      for (int i=0; i < current.children.length; i++){
        if (current.children[i] == null){
          current.children[i] = new Syntax_tree_node(name);
          current.children[i].parent = current;
          current = current.children[i];
          break;
        }
      }
    }
  }

  void move_up_to_parent(){
    if (current.parent != null){
      current = current.parent;
    }
  }

  void print_tree(){
    System.out.println("CST");
    root.print_down_tree(0);
  }

}

class Syntax_tree_node {
  String name;
  Syntax_tree_node parent = null;
  Syntax_tree_node[] children;

  Syntax_tree_node(String name){
    this.name = name;
    children = new Syntax_tree_node[5];
  }

  Syntax_tree_node(String name, Syntax_tree_node parent){
    this.name = name;
    this.parent = parent;
    children = new Syntax_tree_node[5];
  }

  void print_down_tree(int depth){
    String string = "-".repeat(Math.max(0, depth)) +
            " " +
            this.name;
    System.out.println(string);
    for (Syntax_tree_node child : children) {
      if (child != null) {
        child.print_down_tree(depth + 1);
      } else {
        break;
      }
    }
  }
}