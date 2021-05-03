
public class compiler {
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

  public static void main(String[] args) {
    Char_stream char_stream = new Char_stream(System.in);
    parse p = new parse();
    Token_stream token_stream = lex.lexer(char_stream, p, true);
  }
}