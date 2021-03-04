import java.io.InputStream;
import java.util.Scanner;

public class compiler {
  public static void main(String[] args) {
    Char_stream stream = new Char_stream(System.in);
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
