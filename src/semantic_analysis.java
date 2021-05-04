public class semantic_analysis extends compiler {

  static void analysis(Token_stream token_stream, boolean verbose_mode) {
    Syntax_tree tree = new Syntax_tree();

    while (!token_stream.token.equals(END_OF_PROGRAM_TOKEN)){

    }
  }
}

//class AST extends compiler {
//  AST_node root = null;
//  AST_node current = null;
//  int program_numb;
//
//  AST(int program_numb){
//    this.program_numb = program_numb;
//  }
//}
//
//class AST_node extends compiler {
//  String node_name;
//  int line_numb;
//  int line_pos;
//}
//
//class AST_block extends AST_node {
//  int scope;
//  int cur_child;
//  AST_node[] children;
//
//  AST_block(int scope) {
//    this.scope = scope;
//    node_name = grammar_block;
//    cur_child = 0;
//  }
//}
//
//class AST_var_declaration extends AST_node {
//  String var_type;
//  String var_name;
//
//  AST_var_declaration(String var_name, String var_type) {
//    this.node_name = grammar_var_decl;
//    this.var_type = var_type;
//    this.var_name = var_name;
//  }
//}
//
//class AST_var_assign extends AST_node {
//  String var_name;
//  AST_expression value;
//
//  AST_var_assign(String var_name, AST_expression value) {
//    this.node_name = grammar_assignment_statement;
//    this.var_name = var_name;
//    this.value = value;
//  }
//}
//
//class AST_print extends AST_node {
//  AST_expression value;
//
//  AST_print(AST_expression value) {
//    this.node_name = grammar_print_statement;
//    this.value = value;
//  }
//}
//
//class AST_expression extends AST_node {
//  AST_expression left_expr = null;
//  AST_expression right_expr = null;
//
//  AST_expression(String name){
//    this.node_name = name;
//  }
//}

