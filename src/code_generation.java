public class code_generation extends compiler{
  final static short LOAD_ACCUMULATOR_CONSTANT = 0xA9;
  final static short STORE_ACCUMULATOR = 0x8D;

  static void generate_code(AST_tree AST){
    Program program = new Program(AST);
    generate_code_for_layer(AST.root, program);
    program.print_code_hex();
  }
  static void generate_code_for_layer(AST_node cur_node, Program program){
    for (AST_node node : cur_node.children) {
      if (node != null) {
        if (node.name.equals(GRAMMAR_BLOCK)){
          code_generation.generate_code_for_layer(node, program);
        } else if (node.name.equals(GRAMMAR_VAR_DECL)){
          var_decl(node, program);
        }
      }
    }
  }

  static void var_decl(AST_node node, Program program){
    program.add_instruction(LOAD_ACCUMULATOR_CONSTANT);
    program.add_instruction((short) 0x00);// initialize variables to zero
    program.add_instruction(STORE_ACCUMULATOR);
    program.add_instruction(program.new_temp_data(node.children[1].name, node.find_scope()));
    program.add_instruction((short) 0x00);
  }
}

class Program{
  final static int MAX_CODE_SIZE = 256;
  short[] code = new short[MAX_CODE_SIZE];
  int code_pos = 0;
  int heap_pos = MAX_CODE_SIZE - 1;
  Temp_data[] back_patch_data;
  int back_patch_count = 0;

  Program(AST_tree AST){
    back_patch_data = new Temp_data[AST.total_variables];
  }

  void add_instruction(short instruction){
    code[code_pos] = instruction;
    code_pos++;
  }

  short new_temp_data(String var, int scope){
    back_patch_data[back_patch_count] = new Temp_data(var, scope);
    back_patch_count++;
    return back_patch_data[back_patch_count - 1].name;
  }


  int find_temp_data(String desired_var, int scope){
    for (Temp_data temp :
            back_patch_data) {
      if (temp.var.equals(desired_var) && temp.scope == scope) return temp.name;
    }
    return 0;
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
  static short next_new_temp = 0x100; // anything past 0xFF is a temp variable as >= 0x100 is invalid output
  short name;
  String var;
  int scope;
  int offset;

  Temp_data(String var, int scope) {
    this.name = next_new_temp;
    next_new_temp++;
    this.var = var;
    this.scope = scope;
  }
}
