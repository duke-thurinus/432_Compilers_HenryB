
public class compiler {
  public static void main(String[] args) {
    Char_stream char_stream = new Char_stream(System.in);
    parse p = new parse();
    Token_stream token_stream = lex.lexer(char_stream, p, false);
  }
}