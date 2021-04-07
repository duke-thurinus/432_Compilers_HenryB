import java.util.Arrays;

public class parse {
  Token_stream token_stream;
  CST trees;
  CST current_tree;
  static String print_token = "PRINT";
  static String while_token = "WHILE";
  static String if_token = "IF";
  static String type_int_token = "VARIABLE_TYPE [INT]";
  static String type_string_token = "VARIABLE_TYPE [STRING]";
  static String type_bool_token = "VARIABLE_TYPE [BOOL]";
  static String open_bracket_token = "OPEN_BRACKET";
  static String close_bracket_token = "CLOSE_BRACKET";
  static String start_of_program_token = "PROGRAM START";
  static String END_OF_PROGRAM_TOKEN = "END_OF_PROGRAM";
  static String OPEN_PARENTHESISE_TOKEN = "OPEN_PARENTHESISE";
  static String CLOSED_PARENTHESISE_TOKEN = "CLOSED_PARENTHESISE";
  static String assignment_token = "ASSIGNMENT_OP";
  static String equality_token = "EQUALITY_OP";
  static String inequality_token = "INEQUALITY_OP";
  static String addition_op_token = "ADDITION";
  static String quote_mark_token = "QUOTE_MARK";
  static String string_expression = "STRING_EXPRESSION";
  static String[] digit_tokens = {"DIGIT [0]", "DIGIT [1]", "DIGIT [2]", "DIGIT [3]", "DIGIT [4]"
          , "DIGIT [5]", "DIGIT [6]", "DIGIT [7]", "DIGIT [8]", "DIGIT [9]"};
  static String digit_token = "DIGIT TOKEN"; // For error handling
  static String[] ID_tokens = {"ID [a]", "ID [b]", "ID [c]", "ID [d]", "ID [e]", "ID [f]", "ID [g]", "ID [h]"
          , "ID [i]", "ID [j]", "ID [k]", "ID [l]", "ID [m]", "ID [n]", "ID [o]", "ID [p]", "ID [q]", "ID [r]"
          , "ID [s]", "ID [t]", "ID [u]", "ID [v]", "ID [w]", "ID [x]", "ID [y]", "ID [z]"};
  static String ID_token = "ID TOKEN"; // For error handling
  static String[] bool_vals = {"BOOL_VAL [FALSE]", "BOOL_VAL [TRUE]"};
  static String bool_val_token = "BOOL VAL TOKEN"; // For error handling

  // grammar
  static String grammar_program = "PROGRAM";
  static String grammar_block = "BLOCK";
  static String grammar_statement_list = "STATEMENT LIST";
  static String grammar_statement = "STATEMENT";
  static String grammar_print_statement = "PRINT STATEMENT";
  static String grammar_assignment_statement = "ASSIGNMENT STATEMENT";
  static String grammar_var_decl = "VAR DECL";
  static String grammar_while_statement = "WHILE STATEMENT";
  static String grammar_if_statement = "IF STATEMENT";
  static String grammar_expr = "EXPR";
  static String grammar_int_expr = "INT EXPR";
  static String grammar_string_expr = "STRING EXPR";
  static String grammar_bool_expr = "BOOL EXPR";
  static String[] grammar_id = ID_tokens;
  static String grammar_char_list = "CHAR LIST";
  static String grammar_type = "TYPE";
  static String[] grammar_digits = digit_tokens;
  static String grammar_bool_op = "BOOL OP";
  static String grammar_bool_val = "BOOL VAL";
  static String grammar_int_op = "INT OP";

  boolean verbose_mode;


  void parser(Token_stream token_stream, boolean verbose_mode){
    System.out.println(); // Just for spacing output
    this.verbose_mode = verbose_mode;
    this.token_stream = token_stream;
    while (this.token_stream != null && this.token_stream.token.equals(start_of_program_token)){
      if (trees != null){ //not the first program
        trees.next_tree = new CST();
        current_tree = trees.next_tree;
      } else { //first program, create tree
        trees = new CST();
        current_tree = trees;
      }
      trees.program_numb = token_stream.getProgram_numb();
      System.out.println("PARSING --> Program " + token_stream.getProgram_numb());
      this.token_stream = this.token_stream.next_token;
      try {
        parse_program();
        this.current_tree.print_tree();
      } catch (Parse_error e){
        System.out.println("Parse Error at line: " + e.line_numb + " pos: " + e.line_pos +
                " found: " + e.token + " was expecting: " + Arrays.toString(e.expected_tokens));
        while (this.token_stream.next_token != null && !this.token_stream.token.equals(start_of_program_token)){
          this.token_stream = this.token_stream.next_token;
        }
      }
    }
  }

  void parse_program() throws Parse_error {
    if (verbose_mode) System.out.println("parse_program()");
    current_tree.add_node(grammar_program);
    parse_block();
    match(END_OF_PROGRAM_TOKEN);
    current_tree.move_up_to_parent();
  }

  void parse_block() throws Parse_error {
    if (verbose_mode) System.out.println("parse_block()");
    current_tree.add_node(grammar_block);
    match(open_bracket_token);
    parse_statement_list();
    match(close_bracket_token);
    current_tree.move_up_to_parent();
  }

  void parse_statement_list() throws Parse_error {
    if (verbose_mode) System.out.println("parse_statement_list()");
    current_tree.add_node(grammar_statement_list);
    if (!token_stream.token.equals(close_bracket_token)) {
      parse_statement();
      parse_statement_list();
    } else {
      // null accepting
    }
    current_tree.move_up_to_parent();
  }

  void parse_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_statement()");
    current_tree.add_node(grammar_statement);
    if (token_stream.token.equals(print_token)) {
      parse_print_statement();
    } else if (is_ID(token_stream.token)) {
      parse_assignment_statement();
    } else if (token_stream.token.equals(type_int_token) ||
            token_stream.token.equals(type_bool_token) ||
            token_stream.token.equals(type_string_token)) {
      parse_var_decl();
    } else if (token_stream.token.equals(while_token)) {
      parse_while_statement();
    } else if (token_stream.token.equals(if_token)) {
      parse_if_statement();
    } else if (token_stream.token.equals(open_bracket_token)) {
      parse_block();
    } else {
      // ERROR
      throw new Parse_error(token_stream.line_numb, token_stream.line_pos,
              new String[]{print_token, ID_token, type_int_token, type_bool_token, type_string_token, while_token, if_token, open_bracket_token},
              token_stream.token);
    }
    current_tree.move_up_to_parent();
  }

  void parse_print_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_print_statement()");
    current_tree.add_node(grammar_print_statement);
    match(print_token);
    match(OPEN_PARENTHESISE_TOKEN);
    parse_expr();
    match(CLOSED_PARENTHESISE_TOKEN);
    current_tree.move_up_to_parent();
  }

  void parse_assignment_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_assignment_statement()");
    current_tree.add_node(grammar_assignment_statement);
    match(ID_tokens);
    match(assignment_token);
    parse_expr();
    current_tree.move_up_to_parent();
  }

  void parse_var_decl() throws Parse_error {
    if (verbose_mode) System.out.println("parse_var_decl()");
    current_tree.add_node(grammar_var_decl);
    parse_type();
    match(ID_tokens);
    current_tree.move_up_to_parent();
  }

  void parse_while_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_while_statement()");
    current_tree.add_node(grammar_while_statement);
    match(while_token);
    parse_bool_expr();
    parse_block();
    current_tree.move_up_to_parent();
  }

  void parse_if_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_if_statement()");
    current_tree.add_node(grammar_if_statement);
    match(if_token);
    parse_bool_expr();
    parse_block();
    current_tree.move_up_to_parent();
  }

  void parse_expr() throws Parse_error {
    if (verbose_mode) System.out.println("parse_expr()");
    current_tree.add_node(grammar_expr);
    if (is_DIGIT(token_stream.token)){
      parse_int_expr();
    } else if (token_stream.token.equals(type_string_token)){
      parse_string_expr();
    } else if (token_stream.token.equals(type_bool_token)){
      parse_bool_expr();
    } else if (token_stream.token.equals(type_int_token)){
      match(ID_tokens);
    } else {
      // ERROR
      throw new Parse_error(token_stream.line_numb, token_stream.line_pos,
              new String[]{digit_token, type_string_token, type_bool_token, type_int_token},
              token_stream.token);
    }
    current_tree.move_up_to_parent();
  }

  void parse_int_expr() throws Parse_error {
    if (verbose_mode) System.out.println("parse_int_expr()");
    current_tree.add_node(grammar_int_expr);
    match(digit_tokens);
    if (token_stream.token.equals(addition_op_token)){
      parse_addition();
      parse_expr();
    }
    current_tree.move_up_to_parent();
  }

  void parse_string_expr() throws Parse_error {
    if (verbose_mode) System.out.println("parse_string_expr()");
    current_tree.add_node(grammar_string_expr);
    match(quote_mark_token);
    parse_char_list();
    match(quote_mark_token);
    current_tree.move_up_to_parent();
  }

  void parse_bool_expr() throws Parse_error {
    if (verbose_mode) System.out.println("parse_bool_expr()");
    current_tree.add_node(grammar_bool_expr);
    if (token_stream.token.equals(OPEN_PARENTHESISE_TOKEN)) {
      match(OPEN_PARENTHESISE_TOKEN);
      parse_expr();
      parse_bool_op();
      parse_expr();
      match(CLOSED_PARENTHESISE_TOKEN);
    } else if (token_stream.token.equals(bool_vals[0]) || token_stream.token.equals(bool_vals[1])){
      parse_bool_val();
    } else {
      // ERROR
      throw new Parse_error(token_stream.line_numb, token_stream.line_pos,
              new String[]{OPEN_PARENTHESISE_TOKEN, bool_val_token},
              token_stream.token);
    }
    current_tree.move_up_to_parent();
  }

  void parse_type() throws Parse_error {
    if (verbose_mode) System.out.println("parse_type()");
    current_tree.add_node(grammar_type);
    match(new String[]{type_int_token, type_string_token, type_bool_token});
    current_tree.move_up_to_parent();
  }

  void parse_bool_op() throws Parse_error {
    if (verbose_mode) System.out.println("parse_bool_op()");
    current_tree.add_node(grammar_bool_op);
    match(new String[]{equality_token, inequality_token});
    current_tree.move_up_to_parent();
  }

  void parse_bool_val() throws Parse_error {
    if (verbose_mode) System.out.println("parse_bool_val()");
    current_tree.add_node(grammar_bool_val);
    match(bool_vals);
    current_tree.move_up_to_parent();
  }

  void parse_addition() throws Parse_error {
    if (verbose_mode) System.out.println("parse_addition()");
    current_tree.add_node(grammar_int_op);
    match("ADDITION");
    current_tree.move_up_to_parent();
  }

  void parse_char_list() throws Parse_error {
    if (verbose_mode) System.out.println("parse_char_list()");
    current_tree.add_node(grammar_char_list);
    match(string_expression);
    current_tree.move_up_to_parent();
  }

  void match(String token) throws Parse_error{
    if (token_stream.token.equals(token)) {
      current_tree.add_node(token_stream.token);
      token_stream = token_stream.next_token;
      current_tree.move_up_to_parent();
    } else {
      // ERROR
      throw new Parse_error(token_stream.line_numb, token_stream.line_pos,
              new String[]{token},
              token_stream.token);
    }
  }

  void match(String[] tokens) throws Parse_error {
    for (String t : tokens){
      if (t.equals(token_stream.token)){
        current_tree.add_node(token_stream.token);
        token_stream = token_stream.next_token;
        current_tree.move_up_to_parent();
        return;
      }
    }
    // ERROR
    String[] desired_tokens;
    if (tokens[0].equals(digit_tokens[0])){
      desired_tokens = new String[] {digit_token};
    } else if (tokens[0].equals(ID_tokens[0])){
      desired_tokens = new String[] {ID_token};
    } else if (tokens[0].equals(bool_vals[0])){
      desired_tokens = new String[] {bool_val_token};
    } else {
      desired_tokens = tokens;
    }
    throw new Parse_error(token_stream.line_numb, token_stream.line_pos,
            desired_tokens,
            token_stream.token);
  }

  static boolean is_ID(String token){
    if (token.length() < 4) return false;
    return token.substring(0,4).equals("ID [");
  }

  static boolean is_DIGIT(String token){
    if (token.length() < 7) return false;
    return token.substring(0,7).equals("DIGIT [");
  }
}

class CST {
  CST_node root = null;
  CST_node current = null;
  CST next_tree = null;
  int program_numb;

  void add_node(String name){
    if (root == null){
      //make root
      root = new CST_node(name);
      current = root;
    } else {
      //new node
      for (int i=0; i < current.children.length; i++){
        if (current.children[i] == null){
          current.children[i] = new CST_node(name);
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

class CST_node {
  String name;
  CST_node parent = null;
  CST_node[] children;

  CST_node(String name){
    this.name = name;
    children = new CST_node[5];
  }

  CST_node(String name, CST_node parent){
    this.name = name;
    this.parent = parent;
    children = new CST_node[5];
  }

  void print_down_tree(int depth){
    String string = "-".repeat(Math.max(0, depth)) +
            " " +
            this.name;
    System.out.println(string);
    for (CST_node child : children) {
      if (child != null) {
        child.print_down_tree(depth + 1);
      } else {
        break;
      }
    }
  }
}

class Parse_error extends Exception {
  int line_numb;
  int line_pos;
  String[] expected_tokens;
  String token;

  public Parse_error(int line_numb, int line_pos, String[] expected_tokens, String token) {
    this.line_numb = line_numb;
    this.line_pos = line_pos;
    this.expected_tokens = expected_tokens;
    this.token = token;
  }
}
