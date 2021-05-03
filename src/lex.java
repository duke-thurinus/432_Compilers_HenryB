import java.io.InputStream;
import java.util.*;

class lex extends compiler{
  static Token_stream lexer(Char_stream char_stream, parse parser, boolean verbose_mode){
    Graph_vertex head = new Graph_vertex("");


    // build graph of the language grammar
    Graph_vertex.insert(head,"a", ID_tokens[0]);
    Graph_vertex.insert(head,"b", ID_tokens[1]);
    Graph_vertex.insert(head,"c", ID_tokens[2]);
    Graph_vertex.insert(head,"d", ID_tokens[3]);
    Graph_vertex.insert(head,"e", ID_tokens[4]);
    Graph_vertex.insert(head,"f", ID_tokens[5]);
    Graph_vertex.insert(head,"g", ID_tokens[6]);
    Graph_vertex.insert(head,"h", ID_tokens[7]);
    Graph_vertex.insert(head,"i", ID_tokens[8]);
    Graph_vertex.insert(head,"j", ID_tokens[9]);
    Graph_vertex.insert(head,"k", ID_tokens[10]);
    Graph_vertex.insert(head,"l", ID_tokens[11]);
    Graph_vertex.insert(head,"m", ID_tokens[12]);
    Graph_vertex.insert(head,"n", ID_tokens[13]);
    Graph_vertex.insert(head,"o", ID_tokens[14]);
    Graph_vertex.insert(head,"p", ID_tokens[15]);
    Graph_vertex.insert(head,"q", ID_tokens[16]);
    Graph_vertex.insert(head,"r", ID_tokens[17]);
    Graph_vertex.insert(head,"s", ID_tokens[18]);
    Graph_vertex.insert(head,"t", ID_tokens[19]);
    Graph_vertex.insert(head,"u", ID_tokens[20]);
    Graph_vertex.insert(head,"v", ID_tokens[21]);
    Graph_vertex.insert(head,"w", ID_tokens[22]);
    Graph_vertex.insert(head,"x", ID_tokens[23]);
    Graph_vertex.insert(head,"y", ID_tokens[24]);
    Graph_vertex.insert(head,"z", ID_tokens[25]);
    Graph_vertex.insert(head,"0", digit_tokens[0]);
    Graph_vertex.insert(head,"1", digit_tokens[1]);
    Graph_vertex.insert(head,"2", digit_tokens[2]);
    Graph_vertex.insert(head,"3", digit_tokens[3]);
    Graph_vertex.insert(head,"4", digit_tokens[4]);
    Graph_vertex.insert(head,"5", digit_tokens[5]);
    Graph_vertex.insert(head,"6", digit_tokens[6]);
    Graph_vertex.insert(head,"7", digit_tokens[7]);
    Graph_vertex.insert(head,"8", digit_tokens[8]);
    Graph_vertex.insert(head,"9", digit_tokens[9]);
    Graph_vertex.insert(head,"+", addition_op_token);
    Graph_vertex.insert(head,"==", equality_token);
    Graph_vertex.insert(head,"!=", inequality_token);
    Graph_vertex.insert(head,"=",assignment_token);
    Graph_vertex.insert(head,"{", open_bracket_token);
    Graph_vertex.insert(head,"}", close_bracket_token);
    Graph_vertex.insert(head,"(", OPEN_PARENTHESISE_TOKEN);
    Graph_vertex.insert(head,")", CLOSED_PARENTHESISE_TOKEN);
    Graph_vertex.insert(head,"\"", quote_mark_token);
    Graph_vertex.insert(head,"/*", COMMENT_START_TOKEN);
    Graph_vertex.insert(head,"print", print_token);
    Graph_vertex.insert(head,"while", while_token);
    Graph_vertex.insert(head,"if", if_token);
    Graph_vertex.insert(head,"int", type_int_token);
    Graph_vertex.insert(head,"string", type_string_token);
    Graph_vertex.insert(head,"boolean", type_bool_token);
    Graph_vertex.insert(head,"false", bool_vals[0]);
    Graph_vertex.insert(head,"true", bool_vals[1]);
    Graph_vertex.insert(head,"$", END_OF_PROGRAM_TOKEN);

    Graph_vertex.valid_chars.add(' ');
    Graph_vertex.valid_chars.add('\n');
    Graph_vertex.valid_chars.add(Character.MIN_VALUE);

    // lexing
    int program = 0;
    Graph_vertex longest_match = null;
    int next_match_line_numb = 0;
    int next_match_line_pos = 0;
    Graph_vertex current_pos = head;
    Graph_vertex next_pos;
    char current_char;
    boolean in_quotes = false;
    boolean in_comment = false;
    boolean program_error = false;
    boolean start_of_program = true;

    // TODO: some of these variables are unnecessary and can be consolidated
    Program_start_token stream_head = new Program_start_token(0,0, program);
    Token_stream current_stream_pos = stream_head;
    Program_start_token current_program_start = stream_head;
    boolean start_of_file = true;

    do {
      current_char = char_stream.next_char();
      if ((!program_error) && is_in_valid_char(current_char)){
        current_stream_pos.next_token = new Token_stream("ERROR" , char_stream.line_numb,char_stream.line_position);
        current_stream_pos = current_stream_pos.next_token;
        if (current_char == '\t'){
          System.out.println("LEXER--> ERROR AT LINE " + char_stream.line_numb + ": INVALID CHAR [ tab ] if tabs are illegal the debates will finally be over :)");
          current_stream_pos.token_description = "LEXER--> ERROR AT LINE " + char_stream.line_numb + ": INVALID CHAR [ tab ] if tabs are illegal the debates will finally be over :)";
        } else {
          System.out.println("LEXER--> ERROR AT LINE " + char_stream.line_numb + ": INVALID CHAR [ " + current_char + " ]");
          current_stream_pos.token_description = "LEXER--> ERROR AT LINE " + char_stream.line_numb + ": INVALID CHAR [ " + current_char + " ]";
        }
        program_error = true;
      }
      if (!program_error) {
        if (current_char != Character.MIN_VALUE && start_of_program) {
          System.out.println("\n\nLEXER--> LEXING PROGRAM " + program + "...");
          if (!start_of_file){

            current_stream_pos.next_token = new Program_start_token(char_stream.line_numb,char_stream.line_position, program);
            current_stream_pos = current_stream_pos.next_token;
            current_program_start = (Program_start_token) current_stream_pos;
          }
          start_of_program = false;
          start_of_file = false;
        }

        if (in_comment) {
          char last_char = current_char;
          while (last_char != '*' || current_char != '/') {
            last_char = current_char;
            current_char = char_stream.next_char();
            if (current_char == Character.MIN_VALUE) {
              System.out.println("LEXER--> WARNING AT LINE " + char_stream.line_numb + ": Comment Never Closed");
              current_stream_pos.next_token = new Token_stream("WARNING" , char_stream.line_numb,char_stream.line_position);
              current_stream_pos = current_stream_pos.next_token;
              current_stream_pos.token_description = "LEXER--> WARNING AT LINE " + char_stream.line_numb + ": Comment Never Closed";
              break;
            }
          }
          char_stream.clear_history();
          in_comment = false;
        } else if (in_quotes) {
          StringBuilder string_expression = new StringBuilder();
          while (current_char != '"') {
            if (!((current_char >= 'a' && current_char <= 'z') || current_char == ' ')) {
              // only letters allowed in STRING_EXPRESSIONS
              System.out.println("LEXER--> ERROR AT LINE " + char_stream.line_numb + ": INVALID CHAR [ " + current_char + " ] IN STRING_EXPRESSION");
              current_stream_pos.next_token = new Token_stream("ERROR" , char_stream.line_numb,char_stream.line_position);
              current_stream_pos = current_stream_pos.next_token;
              current_stream_pos.token_description = "LEXER--> ERROR AT LINE " + char_stream.line_numb + ": INVALID CHAR [ " + current_char + " ] IN STRING_EXPRESSION";
              program_error = true;
              break;
            }
            string_expression.append(current_char);
            current_char = char_stream.next_char();
            if (current_char == Character.MIN_VALUE) {
              break;
            }
          }
          if (!program_error) {
            char_stream.clear_history();
            if (verbose_mode) {
              System.out.print("LEXER--> STRING_EXPRESSION [" + string_expression + "]"); // output match
              System.out.println(" at line: " + next_match_line_numb + " position: " + next_match_line_pos);
            }
            current_stream_pos.next_token = new Token_stream("STRING_EXPRESSION" , char_stream.line_numb,char_stream.line_position);
            current_stream_pos = current_stream_pos.next_token;
            current_stream_pos.token_description = string_expression.toString();


            if (verbose_mode) {
              System.out.print("LEXER--> " + quote_mark_token);
              System.out.println(" at line: " + char_stream.line_numb + " position: " + char_stream.line_position);
            }
            current_stream_pos.next_token = new Token_stream(quote_mark_token , char_stream.line_numb,char_stream.line_position);
            current_stream_pos = current_stream_pos.next_token;
          }
          in_quotes = false;
        } else {
          if (current_pos.equals(head)) { // save token position when starting to match a new token
            next_match_line_numb = char_stream.line_numb;
            next_match_line_pos = char_stream.line_position;
          }

          next_pos = current_pos.vertices.get(current_char);

          if (next_pos == null) {
            if (longest_match != null) {
              if (longest_match.token.equals(COMMENT_START_TOKEN)) {
                in_comment = true;
              } else if (longest_match.token.equals(END_OF_PROGRAM_TOKEN)) {
                System.out.println("LEXER--> PROGRAM " + program + ": Lex completed with no errors");
                current_stream_pos.next_token = new Token_stream(END_OF_PROGRAM_TOKEN , next_match_line_numb, next_match_line_pos);
                current_stream_pos = current_stream_pos.next_token;
                program++;
                start_of_program = true;
                current_program_start.passed_lex = true;
                parser.parser(current_program_start, verbose_mode);
              } else {
                // output match
                if (verbose_mode) {
                  System.out.print("LEXER--> " + longest_match.token);
                  System.out.println(" at line: " + next_match_line_numb + " position: " + next_match_line_pos);
                }
                current_stream_pos.next_token = new Token_stream(longest_match.token , next_match_line_numb, next_match_line_pos);
                current_stream_pos = current_stream_pos.next_token;
                in_quotes = longest_match.token.equals(quote_mark_token); // check if quote has started
              }
              longest_match = null;
              current_pos = head;
              char_stream.start_using_history();
            } else if (!current_pos.equals(head)){
              //failed to build token
              System.out.println("LEXER--> ERROR AT LINE " + next_match_line_numb + " CHAR " + next_match_line_pos);
              current_stream_pos.next_token = new Token_stream("ERROR" , next_match_line_numb ,next_match_line_pos);
              current_stream_pos = current_stream_pos.next_token;
              program_error = true;
            }
          } else {
            current_pos = next_pos;
            if (!current_pos.token.equals("")) {
              longest_match = current_pos;
              char_stream.clear_history();
            }
          }
        }
      } else {
        System.out.println("Lexer--> PROGRAM " + program + " FAILED LEX");
        current_stream_pos.next_token = new Token_stream(END_OF_PROGRAM_TOKEN , char_stream.line_numb,char_stream.line_position);
        current_stream_pos = current_stream_pos.next_token;

        start_of_program = true;
        program++;
        char_stream.clear_history();
        longest_match = null;
        current_pos = head;
        while (current_char != '$' && current_char != Character.MIN_VALUE) {
          current_char = char_stream.next_char();
        }
        program_error = false;
      }
    } while (current_char != Character.MIN_VALUE);
    if (!start_of_program){
      System.out.println("LEXER--> PROGRAM " + program + ": Lex completed with no errors");
      System.out.println("LEXER--> WARNING: No $ at end of file");
      current_stream_pos.next_token = new Token_stream("WARNING" , char_stream.line_numb,char_stream.line_position);
      current_stream_pos = current_stream_pos.next_token;
      current_stream_pos.token_description = "WARNING: No $ at end of file";
      current_program_start.passed_lex = true;
    }
    return stream_head;
  }
  static boolean is_in_valid_char(char c){
    return !(Graph_vertex.valid_chars.contains(c));
  }
}
class Graph_vertex{
  static Set<Character> valid_chars = new HashSet<Character>();
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
        valid_chars.add(word.charAt(0));
        // this is the end of insert and but last vertex is null
      } else {
        vertex.vertices.put(word.charAt(0), new Graph_vertex(""));
        valid_chars.add(word.charAt(0));
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
        valid_chars.add(word.charAt(i));
        // this is the end of insert and but last vertex is null
      } else {
        vertex.vertices.put(word.charAt(i), new Graph_vertex(""));
        valid_chars.add(word.charAt(i));
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

class Token_stream{
  String token;
  String token_description;
  int line_numb;
  int line_pos;
  Token_stream next_token;

  Token_stream(){}

  Token_stream(String token, int line_numb, int line_pos){
    this.token = token;
    this.line_numb = line_numb;
    this.line_pos = line_pos;
  }

  void print_self(){
    System.out.println(this.token);
    if (next_token != null){
      next_token.print_self();
    }
  }
  int getProgram_numb(){
    return -1;
  }
}

class Program_start_token extends Token_stream{
  int program_numb;
  boolean passed_lex;

  Program_start_token(int line_numb, int line_pos, int program_numb){
    this.token = "PROGRAM START";
    this.line_numb = line_numb;
    this.line_pos = line_pos;
    this.program_numb = program_numb;
    passed_lex = false;
  }
  int getProgram_numb(){
    return program_numb;
  }
}
