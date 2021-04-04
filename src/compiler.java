import java.io.InputStream;
import java.util.*;

public class compiler {
  public static void main(String[] args) {
    Char_stream stream = new Char_stream(System.in);
    lex.lexer(stream);
  }
}