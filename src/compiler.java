
public class compiler {
  public static void main(String[] args) {
    Char_stream char_stream = new Char_stream(System.in);
    Token_stream token_stream = lex.lexer(char_stream);
    parse p = new parse();
    token_stream.print_self();
    p.parser(token_stream);
  }
}