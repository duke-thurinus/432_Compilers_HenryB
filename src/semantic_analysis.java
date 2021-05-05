import java.util.HashMap;

public class semantic_analysis extends compiler {

  static void analysis(AST_tree AST, boolean verbose_mode) {
    System.out.println();
    AST.analyze_tree("AST");
  }
}

class AST_tree extends Syntax_tree {
  AST_node root = null;
  AST_node current = null;

  void analyze_tree(String name){
    System.out.println(name);
    root.analyze_down_tree(0);
  }

  void add_node(String name){
    if (root == null){
      //make root
      root = new AST_node(name);
      current = root;
    } else {
      //new node
      for (int i=0; i < current.children.length; i++){
        if (current.children[i] == null){
          current.children[i] = new AST_node(name);
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
}

class AST_node extends Syntax_tree_node{
  HashMap<String, Variable_data> map;
  AST_node parent = null;
  AST_node[] children;

  AST_node(String name) {
    super(name);
    children = new AST_node[5];
  }

  AST_node(String name, AST_node parent) {
    super(name, parent);
    children = new AST_node[5];
  }

  void analyze_down_tree(int depth){
    String string = "-".repeat(Math.max(0, depth)) +
            " " +
            this.name;
    System.out.println(string);

//    if (this.name.equals()){
//
//    }

    for (AST_node child : children) {
      if (child != null) {
        child.analyze_down_tree(depth + 1);
      } else {
        break;
      }
    }
  }
}

class Variable_data {
  String type;
  boolean initialized;
  boolean used;

  public Variable_data(String type, boolean initialized, boolean used) {
    this.type = type;
    this.initialized = initialized;
    this.used = used;
  }
}