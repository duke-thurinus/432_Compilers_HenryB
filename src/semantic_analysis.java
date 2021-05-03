public class semantic_analysis extends compiler{

  static void analysis(){

  }

  class AST{
    AST_node root;
    AST_node current;
    int program_numb;

  }

  class AST_node{
    String type;
    int line_numb;
    int line_pos;
  }

  class AST_block extends AST_node{
    int cur_child;
    AST_node[] children;

    AST_block(){
      type = grammar_block;
      cur_child = 0;
    }
  }

  class AST_var_declaration extends AST_node{
    String var_name;
    String var_type;

    AST_var_declaration(String var_name, String var_type){
      this.type = grammar_var_decl;
      this.var_name = var_name;
      this.var_type = var_type;
    }
  }

  class AST_var_assign extends AST_node{
    String var_name;
    String var_type;

    AST_var_assign(String var_name, String var_type){
      this.type = grammar_assignment_statement;
      this.var_name = var_name;
      this.var_type = var_type;
    }
  }

  class AST_print extends AST_node{
    String var_name;

    AST_print(String var_name){
      this.type = grammar_print_statement;
      this.var_name = var_name;
    }
  }
}
