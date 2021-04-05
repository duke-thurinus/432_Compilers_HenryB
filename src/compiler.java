import java.io.InputStream;
import java.util.*;

public class compiler {
  public static void main(String[] args) {
    Char_stream char_stream = new Char_stream(System.in);
    Token_stream token_stream = lex.lexer(char_stream);
    token_stream.print_self();
  }
}