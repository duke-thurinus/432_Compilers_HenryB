
public class compiler {
  final static String COMMENT_START_TOKEN = "COMMENT_START";
  final static String PRINT_TOKEN = "PRINT";
  final static String WHILE_TOKEN = "WHILE";
  final static String IF_TOKEN = "IF";
  final static String TYPE_INT_TOKEN = "VARIABLE_TYPE [INT]";
  final static String TYPE_STRING_TOKEN = "VARIABLE_TYPE [STRING]";
  final static String TYPE_BOOL_TOKEN = "VARIABLE_TYPE [BOOL]";
  final static String[] TYPE_TOKEN = {TYPE_INT_TOKEN, TYPE_STRING_TOKEN, TYPE_BOOL_TOKEN};
  final static String OPEN_BRACKET_TOKEN = "OPEN_BRACKET";
  final static String CLOSE_BRACKET_TOKEN = "CLOSE_BRACKET";
  final static String START_OF_PROGRAM_TOKEN = "PROGRAM START";
  final static String END_OF_PROGRAM_TOKEN = "END_OF_PROGRAM";
  final static String OPEN_PARENTHESISE_TOKEN = "OPEN_PARENTHESISE";
  final static String CLOSED_PARENTHESISE_TOKEN = "CLOSED_PARENTHESISE";
  final static String ASSIGNMENT_TOKEN = "ASSIGNMENT_OP";
  final static String EQUALITY_TOKEN = "EQUALITY_OP";
  final static String INEQUALITY_TOKEN = "INEQUALITY_OP";
  final static String ADDITION_OP_TOKEN = "ADDITION";
  final static String QUOTE_MARK_TOKEN = "QUOTE_MARK";
  final static String STRING_EXPRESSION = "STRING_EXPRESSION";
  final static String[] DIGIT_TOKENS = {"DIGIT [0]", "DIGIT [1]", "DIGIT [2]", "DIGIT [3]", "DIGIT [4]"
          , "DIGIT [5]", "DIGIT [6]", "DIGIT [7]", "DIGIT [8]", "DIGIT [9]"};
  final static String DIGIT_TOKEN = "DIGIT TOKEN"; // For error handling
  final static String[] ID_TOKENS = {"ID [a]", "ID [b]", "ID [c]", "ID [d]", "ID [e]", "ID [f]", "ID [g]", "ID [h]"
          , "ID [i]", "ID [j]", "ID [k]", "ID [l]", "ID [m]", "ID [n]", "ID [o]", "ID [p]", "ID [q]", "ID [r]"
          , "ID [s]", "ID [t]", "ID [u]", "ID [v]", "ID [w]", "ID [x]", "ID [y]", "ID [z]"};
  final static String ID_TOKEN = "ID TOKEN"; // For error handling
  final static String[] BOOL_VALS = {"BOOL_VAL [FALSE]", "BOOL_VAL [TRUE]"};
  final static String BOOL_VAL_TOKEN = "BOOL VAL TOKEN"; // For error handling

  // grammar
  final static String GRAMMAR_PROGRAM = "PROGRAM";
  final static String GRAMMAR_BLOCK = "BLOCK";
  final static String GRAMMAR_STATEMENT_LIST = "STATEMENT LIST";
  final static String GRAMMAR_STATEMENT = "STATEMENT";
  final static String GRAMMAR_PRINT_STATEMENT = "PRINT STATEMENT";
  final static String GRAMMAR_ASSIGNMENT_STATEMENT = "ASSIGNMENT STATEMENT";
  final static String GRAMMAR_VAR_DECL = "VAR DECL";
  final static String GRAMMAR_WHILE_STATEMENT = "WHILE STATEMENT";
  final static String GRAMMAR_IF_STATEMENT = "IF STATEMENT";
  final static String GRAMMAR_EXPR = "EXPR";
  final static String GRAMMAR_INT_EXPR = "INT EXPR";
  final static String GRAMMAR_STRING_EXPR = "STRING EXPR";
  final static String GRAMMAR_BOOL_EXPR = "BOOL EXPR";
  final static String[] grammar_id = ID_TOKENS;
  final static String GRAMMAR_CHAR_LIST = "CHAR LIST";
  final static String GRAMMAR_TYPE = "TYPE";
  final static String[] grammar_digits = DIGIT_TOKENS;
  final static String GRAMMAR_BOOL_OP = "BOOL OP";
  final static String GRAMMAR_BOOL_VAL = "BOOL VAL";
  final static String GRAMMAR_INT_OP = "INT OP";

  //errors
  final static String UNDECLARED_ID = "UNDECLARED_ID";
  final static String REDECLARED_ID = "REDECLARED_ID";
  final static String TYPE_MISMATCH = "TYPE_MISMATCH";
  final static String MULTI_BOOL_ERROR = "MULTI_BOOL_ERROR";

  public static void main(String[] args) {
    Char_stream char_stream = new Char_stream(System.in);
    parse p = new parse();
    Token_stream token_stream = lex.lexer(char_stream, p, true);
  }
}