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
    try {
      root.analyze_down_tree(0);
      this.print_symbol_table("Symbol Table");
      code_generation.generate_code(this);
    } catch (Semantic_error semantic_error) {
      if (semantic_error.error_type.equals(UNDECLARED_ID)) {
        System.out.println("Semantic Error: " + semantic_error.token + " on line number: " + semantic_error.line_numb + " was never declared");
      } else if (semantic_error.error_type.contains(TYPE_MISMATCH)){
        System.out.println("Semantic Error: Type Mismatch on line number: " + semantic_error.line_numb
                + " - " + semantic_error.token + " wanted, " + semantic_error.error_type.substring(TYPE_MISMATCH.length()) + " received");
      } else if (semantic_error.error_type.equals(MULTI_BOOL_ERROR)){
        System.out.println("Multi bool error detected on line: " + semantic_error.line_numb + " only one boolean comparison at a time");
      }
    }
  }

  void print_symbol_table(String name){
    System.out.println(name);
    root.print_down_symbol_table(0);
  }

  void add_node(String name, int line_numb){
    if (root == null){
      //make root
      root = new AST_node(name, line_numb);
      current = root;
    } else {
      //new node
      boolean success = false;
      for (int i=0; i < current.children.length; i++){
        if (current.children[i] == null){
          current.children[i] = new AST_node(name, current, line_numb);
          current = current.children[i];
          success = true;
          break;
        }
      }
      if (!success) {
        current = current.add_to_full_children(name, line_numb);
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
  Symbol_table map = null;
  AST_node parent = null;
  AST_node[] children;
  int line_numb;

  AST_node(String name, int line_numb) {
    super(name);
    this.line_numb = line_numb;
    children = new AST_node[5];
    if (name.equals(grammar_block)) map = new Symbol_table(find_scope() + 1);
  }

  AST_node(String name, AST_node parent, int line_numb) {
    super(name);
    this.line_numb = line_numb;
    this.parent = parent;
    children = new AST_node[5];
    if (name.equals(grammar_block)) map = new Symbol_table(find_scope() + 1);
    else map = parent.map;
  }

  int find_scope(){
    if (this.parent != null) return this.parent.map.scope;
    return 0;
  }

  AST_node add_to_full_children(String name, int line_numb){
    int old_size = this.children.length;
    AST_node[] new_children = new AST_node[old_size*2];
    System.arraycopy(this.children, 0, new_children, 0, this.children.length);
    this.children = new_children;
    this.children[old_size] = new AST_node(name, this, line_numb);
    return this.children[old_size];
  }

  void analyze_down_tree(int depth) throws Semantic_error {
    String string = "-".repeat(Math.max(0, depth)) +
            " " +
            this.name;
    System.out.println(string);

    if (this.name.equals(grammar_var_decl)){
      //check if ID has already been declared in this scope
      if (map.containsKey(children[1].name)) throw new Semantic_error(this.line_numb, this.children[1].name, REDECLARED_ID);
      //if not declare it
      map.put(children[1].name, new Variable_data(children[0].name));
    } else if (this.name.equals(grammar_assignment_statement)){
      Variable_data ID = this.find_ID(children[0].name);
      // check that ID has been declared in this or a higher scope
      if (ID == null) throw new Semantic_error(this.line_numb, this.children[0].name, UNDECLARED_ID);
      // make sure the right type is being set
      if (parse.is_ID(children[1].name)){
        String child_ID = this.find_ID(children[1].name).type;
        if (!ID.type.equals(child_ID))
          throw new Semantic_error(this.line_numb, ID.type, TYPE_MISMATCH + child_ID);
      } else {
        if (ID.type.equals(type_bool_token)) {
          if (!parse.is_BOOL(this.children[1].name) && !grammar_bool_expr.equals(this.children[1].name)) {
            throw new Semantic_error(this.line_numb, ID.type, TYPE_MISMATCH + this.children[1].name);
          }
        } else if (ID.type.equals(type_int_token) && !parse.is_DIGIT(this.children[1].name)){
          throw new Semantic_error(this.line_numb, ID.type, TYPE_MISMATCH + this.children[1].name);
        }
      }
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

  Variable_data find_ID(String ID_name){
    Variable_data ID = map.get(ID_name);
    if (ID == null && this.parent != null) ID = this.parent.find_ID(ID_name);
    return ID;
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

class Semantic_error extends Exception {
  int line_numb;
  String token;
  String error_type;

  public Semantic_error(int line_numb, String token, String error_type) {
    this.line_numb = line_numb;
    this.token = token;
    this.error_type = error_type;
  }
}