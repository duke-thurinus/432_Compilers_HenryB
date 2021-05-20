import java.util.HashMap;

public class semantic_analysis extends compiler {

  static void analysis(AST_tree AST, boolean verbose_mode) {
    System.out.println();
    AST.analyze_tree("AST");
    AST.print_symbol_table("Symbol Table");
  }
}

class AST_tree extends Syntax_tree {
  AST_node root = null;
  AST_node current = null;

  void analyze_tree(String name){
    System.out.println(name);
    root.analyze_down_tree(0);
  }

  void print_symbol_table(String name){
    System.out.println(name);
    root.print_down_symbol_table(0);
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
          current.children[i] = new AST_node(name, current);
          current = current.children[i];
          break;
        }
      }
      current.add_to_full_children(name);
    }
  }

  void move_up_to_parent(){
    if (current.parent != null){
      current = current.parent;
    }
  }
}

class AST_node extends Syntax_tree_node{
  Symbol_table map = null;
  AST_node parent = null;
  AST_node[] children;

  AST_node(String name) {
    super(name);
    children = new AST_node[5];
    if (name.equals(grammar_block)) map = new Symbol_table(find_scope() + 1);
  }

  AST_node(String name, AST_node parent) {
    super(name);
    this.parent = parent;
    children = new AST_node[5];
    if (name.equals(grammar_block)) map = new Symbol_table(find_scope() + 1);
    else map = parent.map;
  }

  int find_scope(){
    if (this.parent != null) return this.parent.map.scope;
    return 0;
  }

  void add_to_full_children(String name){
    int old_size = this.children.length;
    AST_node[] new_children = new AST_node[old_size*2];
    System.arraycopy(this.children, 0, new_children, 0, this.children.length);
    this.children = new_children;
    this.children[old_size] = new AST_node(name, this);
  }

  void analyze_down_tree(int depth){
    String string = "-".repeat(Math.max(0, depth)) +
            " " +
            this.name;
    System.out.println(string);

    if (this.name.equals(grammar_var_decl)){
      map.put(children[1].name, new Variable_data(children[0].name));
    } else if (this.name.equals(grammar_assignment_statement)){

    } else if (this.name.equals(grammar_print_statement)){

    } else if (this.name.equals(grammar_while_statement)){

    } else if (this.name.equals(grammar_if_statement)){

    }

    for (AST_node child : children) {
      if (child != null) {
        child.analyze_down_tree(depth + 1);
      } else {
        break;
      }
    }
  }

  void print_down_symbol_table(int depth){
    if (this.name.equals(grammar_block)){
      map.forEach((k,v) -> System.out.println("var: " + k + " of type " + v.type + " in scope " + map.scope));
    }
    for (AST_node child : children) {
      if (child != null) {
        child.print_down_symbol_table(depth + 1);
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

  public Variable_data(String type) {
    this.type = type;
    this.initialized = false;
    this.used = false;
  }
}

class Symbol_table extends HashMap<String, Variable_data>{
  int scope = 0;

  Symbol_table(int scope){
    super();
    this.scope = scope;
  }
}