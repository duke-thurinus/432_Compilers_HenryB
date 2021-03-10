import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;

public class compiler {
  public static void main(String[] args) {
    Char_stream stream = new Char_stream(System.in);
    lex.lexer(stream);
  }
}
class lex{
  static void lexer(Char_stream stream){
    Graph_vertex head = new Graph_vertex("");

    // build graph of the language grammar
    Graph_vertex.insert(head,"a", "ID [a]");
    Graph_vertex.insert(head,"b", "ID [b]");
    Graph_vertex.insert(head,"c", "ID [c]");
    Graph_vertex.insert(head,"d", "ID [d]");
    Graph_vertex.insert(head,"e", "ID [e]");
    Graph_vertex.insert(head,"f", "ID [f]");
    Graph_vertex.insert(head,"g", "ID [g]");
    Graph_vertex.insert(head,"h", "ID [h]");
    Graph_vertex.insert(head,"i", "ID [i]");
    Graph_vertex.insert(head,"j", "ID [j]");
    Graph_vertex.insert(head,"k", "ID [k]");
    Graph_vertex.insert(head,"l", "ID [l]");
    Graph_vertex.insert(head,"m", "ID [m]");
    Graph_vertex.insert(head,"n", "ID [n]");
    Graph_vertex.insert(head,"o", "ID [o]");
    Graph_vertex.insert(head,"p", "ID [p]");
    Graph_vertex.insert(head,"q", "ID [q]");
    Graph_vertex.insert(head,"r", "ID [r]");
    Graph_vertex.insert(head,"s", "ID [s]");
    Graph_vertex.insert(head,"t", "ID [t]");
    Graph_vertex.insert(head,"u", "ID [u]");
    Graph_vertex.insert(head,"v", "ID [v]");
    Graph_vertex.insert(head,"w", "ID [w]");
    Graph_vertex.insert(head,"x", "ID [x]");
    Graph_vertex.insert(head,"y", "ID [y]");
    Graph_vertex.insert(head,"z", "ID [z]");
    Graph_vertex.insert(head,"0", "DIGIT [0]");
    Graph_vertex.insert(head,"1", "DIGIT [1]");
    Graph_vertex.insert(head,"2", "DIGIT [2]");
    Graph_vertex.insert(head,"3", "DIGIT [3]");
    Graph_vertex.insert(head,"4", "DIGIT [4]");
    Graph_vertex.insert(head,"5", "DIGIT [5]");
    Graph_vertex.insert(head,"6", "DIGIT [6]");
    Graph_vertex.insert(head,"7", "DIGIT [7]");
    Graph_vertex.insert(head,"8", "DIGIT [8]");
    Graph_vertex.insert(head,"9", "DIGIT [9]");
    Graph_vertex.insert(head,"+", "ADDITION");
    Graph_vertex.insert(head,"==", "EQUALITY_OP");
    Graph_vertex.insert(head,"!=", "INEQUALITY_OP");
    Graph_vertex.insert(head,"=","ASSIGNMENT_OP");
    Graph_vertex.insert(head,"{", "OPEN_BRACE");
    Graph_vertex.insert(head,"}", "CLOSE_BRACE");
    Graph_vertex.insert(head,"(", "OPEN_PARENTHESISE");
    Graph_vertex.insert(head,")", "CLOSED_PARENTHESISE");
    Graph_vertex.insert(head,"\"", "QUOTE_MARK");
    Graph_vertex.insert(head,"/*", "COMMENT_START");
    Graph_vertex.insert(head,"print", "PRINT");
    Graph_vertex.insert(head,"while", "WHILE");
    Graph_vertex.insert(head,"if", "IF");
    Graph_vertex.insert(head,"int", "VARIABLE_TYPE [INT]");
    Graph_vertex.insert(head,"string", "VARIABLE_TYPE [STRING]");
    Graph_vertex.insert(head,"boolean", "VARIABLE_TYPE [BOOL]");
    Graph_vertex.insert(head,"false", "BOOL_VAL [FALSE]");
    Graph_vertex.insert(head,"true", "BOOL_VAL [TRUE]");

    // lexing
    Graph_vertex longest_match = null;
    Graph_vertex current_pos = head;
    Graph_vertex next_pos;
    char current_char;

    do {
      current_char = stream.next_char();
      if (is_invalid_char(current_char)){
        break;
      }

      next_pos = current_pos.vertices.get(current_char);

      if (next_pos == null){
        if (longest_match != null) {
          System.out.println(longest_match.token);
          longest_match = null;
          current_pos = head;
          stream.start_using_history();
        }
      } else {
        current_pos = next_pos;
        if (!current_pos.token.equals("")){
          longest_match = current_pos;
          stream.clear_history();
        }
      }
    } while (current_char != Character.MIN_VALUE);

  }
  static boolean is_invalid_char(char c){
    //TODO write function
    return false;
  }
}
class Graph_vertex{
  String token;
  HashMap<Character, Graph_vertex> vertices;

  Graph_vertex(String token){
    this.token = token;
    vertices = new HashMap<>();
  }
  static void insert(Graph_vertex vertex, String word, String token_description){
    Graph_vertex lower_vertex = vertex.vertices.get(word.charAt(0));
    if (lower_vertex == null){
      if (word.length() == 1) {
        vertex.vertices.put(word.charAt(0), new Graph_vertex(token_description));
        // this is the end of insert and but last vertex is null
      } else {
        vertex.vertices.put(word.charAt(0), new Graph_vertex(""));
        insert(vertex.vertices.get(word.charAt(0)), word, token_description, 1);
        // next vertex is null but it is not the last
      }
    } else {
      if (word.length() == 1){
        lower_vertex.token = token_description;
      } else {
        insert(lower_vertex, word, token_description, 1);
        // next vertex exists and there is more after
      }
    }
  }

  static void insert(Graph_vertex vertex, String word, String token_description, int i){
    Graph_vertex lower_vertex = vertex.vertices.get(word.charAt(i));
    if (lower_vertex == null){
      if (word.length() <= i+1) {
        vertex.vertices.put(word.charAt(i), new Graph_vertex(token_description));
        // this is the end of insert and but last vertex is null
      } else {
        vertex.vertices.put(word.charAt(i), new Graph_vertex(""));
        insert(vertex.vertices.get(word.charAt(i)), word, token_description, i+1);
        // next vertex is null but it is not the last
      }
    } else {
      if (word.length() <= i+1){
        lower_vertex.token = token_description;
      } else {
        insert(lower_vertex, word, token_description, i+1);
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
  // the history allows us to backtrack the char stream
  // lexer uses this to backtrack to right after a successful match
  Queue<Character> history;
  boolean pulling_from_history;
  int history_line_position;

  Char_stream(InputStream source){
    this.source = new Scanner(source);
    if (this.source.hasNext()) {
      line = this.source.nextLine();
      line_numb = 1;
      line_position = 0;
    }
    history = new LinkedList<>();
    pulling_from_history = false;
  }

  char next_char(){
    if (pulling_from_history){
      char c = history.poll();
      if (history.size() <= 0) {
        pulling_from_history = false;
      }
      line_position++;
      return c;
    }
    else if (line_position < line.length()){// plus one cause compensates for String start at 1 but line numbering starts at 1
      line_position++;
      char c = line.charAt(line_position - 1); // minus one for increase second minus one cause String starts at zero but line numbering starts at 1
      if (history.size() == 0){
        history_line_position = line_position;
      }
      history.add(c);
      return c;
    } else if (source.hasNext()){
      line = source.nextLine();
      line_numb++;
      line_position = 0;
      return '\n';
    } else{
      return Character.MIN_VALUE; // end of file
    }
  }
  void clear_history(){
    if (pulling_from_history){
      history_line_position = line_position;
    } else {
      history.clear();
    }
  }
  void start_using_history(){
    // after calling this line_position is meaningless until another char is called
    if (history.size() > 0) {
      pulling_from_history = true;
      line_position = history_line_position - 1;
    }
  }
}
