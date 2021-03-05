import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class compiler {
  public static void main(String[] args) {
    Char_stream stream = new Char_stream(System.in);
    lex.lexer();
  }
}
class lex{
  static void lexer(){
    Graph_vertex head = new Graph_vertex("");
  }
}
class Graph_vertex{
  String token;
  HashMap<Character, Graph_vertex> vertices;

  Graph_vertex(String token){
    this.token = token;
    vertices = new HashMap<>();
  }
  static void insert(Graph_vertex vertex, String word){
    Graph_vertex lower_vertex = vertex.vertices.get(word.charAt(0));
    if (lower_vertex == null){
      if (word.length() == 1) {
        vertex.vertices.put(word.charAt(0), new Graph_vertex(word));
        // this is the end of insert and but last vertex is null
      } else {
        vertex.vertices.put(word.charAt(0), new Graph_vertex(""));
        insert(vertex.vertices.get(word.charAt(0)), word, 1);
        // next vertex is null but it is not the last
      }
    } else {
      if (word.length() == 1){
        lower_vertex.token = word;
      } else {
        insert(lower_vertex, word, 1);
        // next vertex exists and there is more after
      }
    }
  }

  static void insert(Graph_vertex vertex, String word, int i){
    Graph_vertex lower_vertex = vertex.vertices.get(word.charAt(i));
    if (lower_vertex == null){
      if (word.length() <= i+1) {
        vertex.vertices.put(word.charAt(i), new Graph_vertex(word));
        // this is the end of insert and but last vertex is null
      } else {
        vertex.vertices.put(word.charAt(i), new Graph_vertex(""));
        insert(vertex.vertices.get(word.charAt(i)), word, i+1);
        // next vertex is null but it is not the last
      }
    } else {
      if (word.length() <= i+1){
        lower_vertex.token = word;
      } else {
        insert(lower_vertex, word, i+1);
        // next vertex exists and there is more after
      }
    }
  }
}

class Char_stream {
  Scanner source;
  int line_numb;
  int line_position;
  String line;

  Char_stream(InputStream source){
    this.source = new Scanner(source);
    if (this.source.hasNext()) {
      line = this.source.nextLine();
      line_numb = 1;
      line_position = 0;
    }
  }

  char next_char(){
    if (line_position < line.length()){// plus one cause compensates for String start at 1 but line numbering starts at 1
      line_position++;
      return line.charAt(line_position - 1);// minus one for increase second minus one cause String starts at zero but line numbering starts at 1
    } else if (source.hasNext()){
      line = source.nextLine();
      line_numb++;
      line_position = 0;
      return '\n';
    } else{
      return Character.MIN_VALUE;
    }
  }
}
