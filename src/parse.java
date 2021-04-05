public class parse {
  Token_stream token_stream;
  static String print_token = "PRINT";
  static String while_token = "WHILE";
  static String if_token = "IF";
  static String type_int_token = "VARIABLE_TYPE [INT]";
  static String type_string_token = "VARIABLE_TYPE [STRING]";
  static String type_bool_token = "VARIABLE_TYPE [BOOL]";
  static String open_brace_token = "OPEN_BRACE";
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
  static String[] ID_tokens = {"ID [a]", "ID [b]", "ID [c]", "ID [d]", "ID [e]", "ID [f]", "ID [g]", "ID [h]"
          , "ID [i]", "ID [j]", "ID [k]", "ID [l]", "ID [m]", "ID [n]", "ID [o]", "ID [p]", "ID [q]", "ID [r]"
          , "ID [s]", "ID [t]", "ID [u]", "ID [v]", "ID [w]", "ID [x]", "ID [y]", "ID [z]"};
  static String[] bool_vals = {"BOOL_VAL [FALSE]", "BOOL_VAL [TRUE]"};


  void parser(Token_stream token_stream){
    this.token_stream = token_stream;
    while (token_stream.token.equals(start_of_program_token)){
      System.out.println("PARSING --> Program " + token_stream.getProgram_numb());
      this.token_stream = this.token_stream.next_token;
      parse_program();
    }
  }

  void parse_program(){
    parse_block();
    match("$");
  }

  void parse_block(){
    match("{");
    parse_statement_list();
    match("}");
  }

  void parse_statement_list(){
    if (!token_stream.token.equals(END_OF_PROGRAM_TOKEN)) {
      parse_statement();
      parse_statement_list();
    } else {
      // null accepting
    }
  }

  void parse_statement(){
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
    } else if (token_stream.token.equals(open_brace_token)) {
      parse_block();
    } else {
      //todo: throw error
    }
  }

  void parse_print_statement(){
    match(print_token);
    match(OPEN_PARENTHESISE_TOKEN);
    parse_expr();
    match(CLOSED_PARENTHESISE_TOKEN);
  }

  void parse_assignment_statement(){
    parse_id();
    match(assignment_token);
    parse_expr();
  }

  void parse_var_decl(){
    parse_type();
    parse_id();
  }

  void parse_while_statement(){
    match(while_token);
    parse_bool_expr();
    parse_block();
  }

  void parse_if_statement(){
    match(if_token);
    parse_bool_expr();
    parse_block();
  }

  void parse_expr(){
    if (is_DIGIT(token_stream.token)){
      parse_int_expr();
    } else if (token_stream.token.equals(type_string_token)){
      parse_string_expr();
    } else if (token_stream.token.equals(type_bool_token)){
      parse_bool_expr();
    } else if (token_stream.token.equals(type_int_token)){
      parse_id();
    } else {
      //todo: throw errors
    }
  }

  void parse_int_expr(){
    parse_digit();
    if (token_stream.token.equals(addition_op_token)){
      parse_addition();
      parse_expr();
    }
  }

  void parse_string_expr(){
    match(quote_mark_token);
    parse_char_list();
    match(quote_mark_token);
  }

  void parse_bool_expr(){
    if (token_stream.token.equals(OPEN_PARENTHESISE_TOKEN)) {
      match(OPEN_PARENTHESISE_TOKEN);
      parse_expr();
      parse_bool_op();
      parse_expr();
      match(CLOSED_PARENTHESISE_TOKEN);
    } else if (token_stream.token.equals(bool_vals[0]) || token_stream.token.equals(bool_vals[1])){
      parse_bool_val();
    } else {
      //todo: throw error
    }
  }

  void parse_id(){
    match(ID_tokens);
  }

  void parse_type(){
    match(new String[]{type_int_token, type_string_token, type_bool_token});
  }

  void parse_digit(){
    match(digit_tokens);
  }

  void parse_bool_op(){
    match(new String[]{equality_token, inequality_token});
  }

  void parse_bool_val(){
    match(bool_vals);
  }

  void parse_addition(){
    match("ADDITION");
  }

  void parse_char_list(){
    match(string_expression);
  }

  void match(String token){
    token_stream = token_stream.next_token;
    //todo: this is temp, need to actually build
  }

  void match(String[] tokens){
    token_stream = token_stream.next_token;
    //todo: this is temp, need to actually build
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
}
