import java.util.Arrays;

public class parse extends compiler{
  Token_stream token_stream;
  Syntax_tree trees;
  Syntax_tree current_tree;
  boolean verbose_mode;
  AST_tree AST;


  void parser(Token_stream token_stream, boolean verbose_mode){
    System.out.println(); // Just for spacing output
    this.verbose_mode = verbose_mode;
    this.token_stream = token_stream;
    AST = new AST_tree();
    while (this.token_stream != null && this.token_stream.token.equals(START_OF_PROGRAM_TOKEN)){
      if (trees != null){ //not the first program
        trees.next_tree = new Syntax_tree();
        current_tree = trees.next_tree;
      } else { //first program, create tree
        trees = new Syntax_tree();
        current_tree = trees;
      }
      trees.program_numb = token_stream.getProgram_numb();
      System.out.println("PARSING --> Program " + token_stream.getProgram_numb());
      this.token_stream = this.token_stream.next_token;
      try {
        parse_program();
        this.current_tree.print_tree("CST");
        semantic_analysis.analysis(AST, verbose_mode);
      } catch (Parse_error e){
        System.out.println("Parse Error at line: " + e.line_numb + " pos: " + e.line_pos +
                " found: " + e.token + " could have accepted " + Arrays.toString(e.expected_tokens));
        while (this.token_stream.next_token != null && !this.token_stream.token.equals(START_OF_PROGRAM_TOKEN)){
          this.token_stream = this.token_stream.next_token;
        }
      }
    }
  }

  void parse_program() throws Parse_error {
    if (verbose_mode) System.out.println("parse_program()");
    current_tree.add_node(GRAMMAR_PROGRAM);
    parse_block();
    match(END_OF_PROGRAM_TOKEN);
    current_tree.move_up_to_parent();
  }

  void parse_block() throws Parse_error {
    if (verbose_mode) System.out.println("parse_block()");
    current_tree.add_node(GRAMMAR_BLOCK);
    AST.add_node(GRAMMAR_BLOCK, token_stream.line_numb);
    match(OPEN_BRACKET_TOKEN);
    parse_statement_list();
    match(CLOSE_BRACKET_TOKEN);
    current_tree.move_up_to_parent();
    AST.move_up_to_parent();
  }

  void parse_statement_list() throws Parse_error {
    if (verbose_mode) System.out.println("parse_statement_list()");
    current_tree.add_node(GRAMMAR_STATEMENT_LIST);
    if (!token_stream.token.equals(CLOSE_BRACKET_TOKEN)) {
      parse_statement();
      parse_statement_list();
    } else {
      // null accepting
    }
    current_tree.move_up_to_parent();
  }

  void parse_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_statement()");
    current_tree.add_node(GRAMMAR_STATEMENT);
    if (token_stream.token.equals(PRINT_TOKEN)) {
      parse_print_statement();
    } else if (is_ID(token_stream.token)) {
      parse_assignment_statement();
    } else if (token_stream.token.equals(TYPE_INT_TOKEN) ||
            token_stream.token.equals(TYPE_BOOL_TOKEN) ||
            token_stream.token.equals(TYPE_STRING_TOKEN)) {
      parse_var_decl();
    } else if (token_stream.token.equals(WHILE_TOKEN)) {
      parse_while_statement();
    } else if (token_stream.token.equals(IF_TOKEN)) {
      parse_if_statement();
    } else if (token_stream.token.equals(OPEN_BRACKET_TOKEN)) {
      parse_block();
    } else {
      // ERROR
      throw new Parse_error(token_stream.line_numb, token_stream.line_pos,
              new String[]{PRINT_TOKEN, ID_TOKEN, TYPE_INT_TOKEN, TYPE_BOOL_TOKEN, TYPE_STRING_TOKEN, WHILE_TOKEN, IF_TOKEN, OPEN_BRACKET_TOKEN},
              token_stream.token);
    }
    current_tree.move_up_to_parent();
  }

  void parse_print_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_print_statement()");
    current_tree.add_node(GRAMMAR_PRINT_STATEMENT);
    AST.add_node(GRAMMAR_PRINT_STATEMENT, token_stream.line_numb);
    match(PRINT_TOKEN);
    match(OPEN_PARENTHESISE_TOKEN);
    parse_expr();
    match(CLOSED_PARENTHESISE_TOKEN);
    current_tree.move_up_to_parent();
  }

  void parse_assignment_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_assignment_statement()");
    current_tree.add_node(GRAMMAR_ASSIGNMENT_STATEMENT);
    AST.add_node(GRAMMAR_ASSIGNMENT_STATEMENT, token_stream.line_numb);
    match(ID_TOKENS);
    match(ASSIGNMENT_TOKEN);
    parse_expr();
    current_tree.move_up_to_parent();
    AST.move_up_to_parent();
  }

  void parse_var_decl() throws Parse_error {
    if (verbose_mode) System.out.println("parse_var_decl()");
    current_tree.add_node(GRAMMAR_VAR_DECL);
    AST.add_node(GRAMMAR_VAR_DECL, token_stream.line_numb);
    parse_type();
    match(ID_TOKENS);
    current_tree.move_up_to_parent();
    AST.move_up_to_parent();
  }

  void parse_while_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_while_statement()");
    current_tree.add_node(GRAMMAR_WHILE_STATEMENT);
    AST.add_node(GRAMMAR_WHILE_STATEMENT, token_stream.line_numb);
    match(WHILE_TOKEN);
    parse_bool_expr();
    parse_block();
    current_tree.move_up_to_parent();
    AST.move_up_to_parent();
  }

  void parse_if_statement() throws Parse_error {
    if (verbose_mode) System.out.println("parse_if_statement()");
    current_tree.add_node(GRAMMAR_IF_STATEMENT);
    AST.add_node(GRAMMAR_IF_STATEMENT, token_stream.line_numb);
    match(IF_TOKEN);
    parse_bool_expr();
    parse_block();
    current_tree.move_up_to_parent();
    AST.move_up_to_parent();
  }

  void parse_expr() throws Parse_error {
    if (verbose_mode) System.out.println("parse_expr()");
    current_tree.add_node(GRAMMAR_EXPR);
    if (is_DIGIT(token_stream.token)){
      parse_int_expr();
    } else if (token_stream.token.equals(QUOTE_MARK_TOKEN)){
      parse_string_expr();
    } else if (token_stream.token.equals(OPEN_PARENTHESISE_TOKEN) || is_BOOL(token_stream.token)){
      parse_bool_expr();
    } else if (is_ID(token_stream.token)){
      match(ID_TOKENS);
    } else {
      // ERROR
      throw new Parse_error(token_stream.line_numb, token_stream.line_pos,
              new String[]{DIGIT_TOKEN, QUOTE_MARK_TOKEN, TYPE_BOOL_TOKEN, TYPE_INT_TOKEN},
              token_stream.token);
    }
    current_tree.move_up_to_parent();
  }

  void parse_int_expr() throws Parse_error {
    if (verbose_mode) System.out.println("parse_int_expr()");
    current_tree.add_node(GRAMMAR_INT_EXPR);
    match(DIGIT_TOKENS);
    if (token_stream.token.equals(ADDITION_OP_TOKEN)){
      parse_addition();
      parse_expr();
    }
    current_tree.move_up_to_parent();
  }

  void parse_string_expr() throws Parse_error {
    if (verbose_mode) System.out.println("parse_string_expr()");
    current_tree.add_node(GRAMMAR_STRING_EXPR);
    AST.add_node(TYPE_STRING_TOKEN, token_stream.line_numb);
    match(QUOTE_MARK_TOKEN);
    parse_char_list();
    match(QUOTE_MARK_TOKEN);
    current_tree.move_up_to_parent();
  }

  void parse_bool_expr() throws Parse_error {
    if (verbose_mode) System.out.println("parse_bool_expr()");
    current_tree.add_node(GRAMMAR_BOOL_EXPR);
    if (token_stream.token.equals(OPEN_PARENTHESISE_TOKEN)) {
      AST.add_node(GRAMMAR_BOOL_EXPR, token_stream.line_numb);
      match(OPEN_PARENTHESISE_TOKEN);
      parse_expr();
      parse_bool_op();
      parse_expr();
      match(CLOSED_PARENTHESISE_TOKEN);
      AST.move_up_to_parent();
    } else if (token_stream.token.equals(BOOL_VALS[0]) || token_stream.token.equals(BOOL_VALS[1])){
      parse_bool_val();
    } else {
      // ERROR
      throw new Parse_error(token_stream.line_numb, token_stream.line_pos,
              new String[]{OPEN_PARENTHESISE_TOKEN, BOOL_VAL_TOKEN},
              token_stream.token);
    }
    current_tree.move_up_to_parent();
  }

  void parse_type() throws Parse_error {
    if (verbose_mode) System.out.println("parse_type()");
    current_tree.add_node(GRAMMAR_TYPE);
    match(new String[]{TYPE_INT_TOKEN, TYPE_STRING_TOKEN, TYPE_BOOL_TOKEN});
    current_tree.move_up_to_parent();
  }

  void parse_bool_op() throws Parse_error {
    if (verbose_mode) System.out.println("parse_bool_op()");
    current_tree.add_node(GRAMMAR_BOOL_OP);
    match(new String[]{EQUALITY_TOKEN, INEQUALITY_TOKEN});
    current_tree.move_up_to_parent();
  }

  void parse_bool_val() throws Parse_error {
    if (verbose_mode) System.out.println("parse_bool_val()");
    current_tree.add_node(GRAMMAR_BOOL_VAL);
    match(BOOL_VALS);
    current_tree.move_up_to_parent();
  }

  void parse_addition() throws Parse_error {
    if (verbose_mode) System.out.println("parse_addition()");
    current_tree.add_node(GRAMMAR_INT_OP);
    match(ADDITION_OP_TOKEN);
    current_tree.move_up_to_parent();
  }

  void parse_char_list() throws Parse_error {
    if (verbose_mode) System.out.println("parse_char_list()");
    current_tree.add_node(GRAMMAR_CHAR_LIST);
    match(STRING_EXPRESSION);
    current_tree.move_up_to_parent();
  }

  void match(String token) throws Parse_error{
    if (token_stream.token.equals(token)) {
      current_tree.add_node(token_stream.token);
      if (Arrays.asList(DIGIT_TOKENS).contains(token_stream.token) ||
              Arrays.asList(ID_TOKENS).contains(token_stream.token) ||
              Arrays.asList(TYPE_TOKEN).contains(token_stream.token) ||
              Arrays.asList(BOOL_VALS).contains(token_stream.token) ||
              EQUALITY_TOKEN.equals(token_stream.token) ||
              INEQUALITY_TOKEN.equals(token_stream.token) ||
              ADDITION_OP_TOKEN.equals(token_stream.token)) {
        AST.add_node(token_stream.token, token_stream.line_numb);
        AST.move_up_to_parent();
      } else if (STRING_EXPRESSION.equals(token_stream.token)){
        AST.add_node(token_stream.token_description, token_stream.line_numb);
        AST.move_up_to_parent();
      }
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
        if (Arrays.asList(DIGIT_TOKENS).contains(token_stream.token) ||
                Arrays.asList(ID_TOKENS).contains(token_stream.token) ||
                Arrays.asList(TYPE_TOKEN).contains(token_stream.token) ||
                Arrays.asList(BOOL_VALS).contains(token_stream.token) ||
                EQUALITY_TOKEN.equals(token_stream.token) || INEQUALITY_TOKEN.equals(token_stream.token)) {
          AST.add_node(token_stream.token, token_stream.line_numb);
          AST.move_up_to_parent();
        }
        current_tree.add_node(token_stream.token);
        token_stream = token_stream.next_token;
        current_tree.move_up_to_parent();
        return;
      }
    }
    // ERROR
    String[] desired_tokens;
    if (tokens[0].equals(DIGIT_TOKENS[0])){
      desired_tokens = new String[] {DIGIT_TOKEN};
    } else if (tokens[0].equals(ID_TOKENS[0])){
      desired_tokens = new String[] {ID_TOKEN};
    } else if (tokens[0].equals(BOOL_VALS[0])){
      desired_tokens = new String[] {BOOL_VAL_TOKEN};
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

  static  boolean is_BOOL(String token){
    return (token.equals(BOOL_VALS[0]) || token.equals(BOOL_VALS[1]));
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
