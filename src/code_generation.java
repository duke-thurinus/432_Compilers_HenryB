public class code_generation extends compiler{
  static void generate_code(AST_tree AST){
    Program program = new Program();
    
    program.print_code_hex();
  }
}

class Program{
  static int MAX_CODE_SIZE = 256;
  byte[] code = new byte[MAX_CODE_SIZE];
  int code_pos = 0;
  int heap_pos = MAX_CODE_SIZE;

  void print_code_hex(){
    System.out.println();
    int counter = 0;
    for (byte byt: code) {
      if (counter >= 8){
        System.out.println();
        counter = 0;
      }
      System.out.print(String.format("%02x", byt) + " ");
      counter++;
    }
  }
}
