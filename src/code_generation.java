public class code_generation extends compiler{
  static void generate_code(AST_tree AST){
    Program program = new Program(AST);
    AST.current = AST.root;
    program.print_code_hex();
  }
  static void generate_code_for_layer(AST_node cur_node){
    for (AST_node node : cur_node.children) {
      if (node.name.equals(grammar_block)){
        code_generation.generate_code_for_layer(node);
      }
    }
  }
}

class Program{
  final static int MAX_CODE_SIZE = 256;
  short[] code = new short[MAX_CODE_SIZE];
  int code_pos = 0;
  int heap_pos = MAX_CODE_SIZE;
  Temp_data[] back_patch_data;

  Program(AST_tree AST){
    back_patch_data = new Temp_data[AST.total_variables];
  }

  void print_code_hex(){
    System.out.println();
    int counter = 0;
    for (short byt: code) {
      if (counter >= 8){
        System.out.println();
        counter = 0;
      }
      System.out.print(String.format("%02x", byt) + " ");
      counter++;
    }
  }
}

class Temp_data{
  static int next_new_temp = 0x100; // anything past 0xFF is a temp variable as >= 0x100 is invalid output
  int name;
  String var;
  int scope;
  int offset;

  Temp_data(String var, int scope, int offset) {
    this.name = next_new_temp;
    next_new_temp++;
    this.var = var;
    this.scope = scope;
    this.offset = offset;
  }
}
