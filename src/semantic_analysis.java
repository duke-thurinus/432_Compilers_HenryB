import java.util.HashMap;

public class semantic_analysis extends compiler {

  static void analysis(AST_tree AST, boolean verbose_mode) {
    System.out.println();
    AST.print_tree("AST");
  }
}

class AST_tree extends Syntax_tree {

}

class AST_node extends Syntax_tree_node{
  HashMap<String, Variable_data> map;

  AST_node(String name) {
    super(name);
  }

  AST_node(String name, Syntax_tree_node parent) {
    super(name, parent);
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