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

    // build graph
    Graph_vertex.insert(head,"a");
    Graph_vertex.insert(head,"b");
    Graph_vertex.insert(head,"c");
    Graph_vertex.insert(head,"d");
    Graph_vertex.insert(head,"e");
    Graph_vertex.insert(head,"f");
    Graph_vertex.insert(head,"g");
    Graph_vertex.insert(head,"h");
    Graph_vertex.insert(head,"i");
    Graph_vertex.insert(head,"j");
    Graph_vertex.insert(head,"k");
    Graph_vertex.insert(head,"l");
    Graph_vertex.insert(head,"m");
    Graph_vertex.insert(head,"n");
    Graph_vertex.insert(head,"o");
    Graph_vertex.insert(head,"p");
    Graph_vertex.insert(head,"q");
    Graph_vertex.insert(head,"r");
    Graph_vertex.insert(head,"s");
    Graph_vertex.insert(head,"t");
    Graph_vertex.insert(head,"u");
    Graph_vertex.insert(head,"v");
    Graph_vertex.insert(head,"w");
    Graph_vertex.insert(head,"x");
    Graph_vertex.insert(head,"y");
    Graph_vertex.insert(head,"z");
    Graph_vertex.insert(head,"0");
    Graph_vertex.insert(head,"1");
    Graph_vertex.insert(head,"2");
    Graph_vertex.insert(head,"3");
    Graph_vertex.insert(head,"4");
    Graph_vertex.insert(head,"5");
    Graph_vertex.insert(head,"6");
    Graph_vertex.insert(head,"7");
    Graph_vertex.insert(head,"8");
    Graph_vertex.insert(head,"9");
    Graph_vertex.insert(head,"+");
    Graph_vertex.insert(head,"==");
    Graph_vertex.insert(head,"!=");
    Graph_vertex.insert(head,"{");
    Graph_vertex.insert(head,"}");
    Graph_vertex.insert(head,"(");
    Graph_vertex.insert(head,")");
    Graph_vertex.insert(head,"\"");
    Graph_vertex.insert(head,"/*");
    Graph_vertex.insert(head," ");
    Graph_vertex.insert(head,"print");
    Graph_vertex.insert(head,"while");
    Graph_vertex.insert(head,"if");
    Graph_vertex.insert(head,"int");
    Graph_vertex.insert(head,"string");
    Graph_vertex.insert(head,"boolean");
    Graph_vertex.insert(head,"false");
    Graph_vertex.insert(head,"true");
    System.out.println();
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
