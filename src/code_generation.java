import java.util.Arrays;

public class code_generation extends compiler{
  final static short LOAD_ACCUMULATOR_CONSTANT = 0xA9;
  final static short LOAD_ACCUMULATOR_MEMORY = 0xAD;
  final static short STORE_ACCUMULATOR = 0x8D;

  static void generate_code(AST_tree AST, boolean verbose_mode){
    Program program = new Program(AST);
    generate_code_for_layer(AST.root, program);
    back_patch(program);
    if (program.code_stack_pos > program.heap_pos + 1){
      System.out.println("Code Generation Error: Program has exceeded the " + Program.MAX_CODE_SIZE + " byte size limit");
    } else {
      program.print_code_hex();
      if (verbose_mode){
        program.print_code_hex_human_readable();
      }
    }
  }
  static void generate_code_for_layer(AST_node cur_node, Program program){
    for (AST_node node : cur_node.children) {
      if (node != null) {
        if (node.name.equals(GRAMMAR_BLOCK)){
          code_generation.generate_code_for_layer(node, program);
        } else if (node.name.equals(GRAMMAR_VAR_DECL)){
          var_decl(node, program);
        } else if (node.name.equals(GRAMMAR_ASSIGNMENT_STATEMENT)){
          assignment(node, program);
        }
      }
    }
    // end of code break
    program.add_instruction((short) 0x00);
  }

  static void back_patch(Program program){
    int end_of_code = program.code_stack_pos;
    for (Temp_data data : program.back_patch_data) {
      data.address = (short) program.code_stack_pos;
      program.code_stack_pos++;
    }
    for (int i = 0; i < program.code_stack_pos; i++) {
      if (program.code[i] > 0xFF){
        for (Temp_data data: program.back_patch_data){
          if (program.code[i] == data.name) program.code[i] = data.address;
        }
      }
    }
  }

  static void var_decl(AST_node node, Program program){
    program.load_accumulator((short)0x00);
    program.store_accumulator(program.new_temp_data(node.children[1].name, node.find_scope()));
  }

  static void assignment(AST_node node, Program program){
    if (Arrays.asList(DIGIT_TOKENS).contains(node.children[1].name)){
      // int assignment
      if (node.children[2] == null){
        //single assignment
        program.load_accumulator((short) Arrays.asList(DIGIT_TOKENS).indexOf(node.children[1].name));
        program.store_accumulator(program.find_temp_data(node.children[0].name, node.find_scope()));
      } else {
        // addition then assignment
      }
    } else if (Arrays.asList(BOOL_VALS).contains(node.children[1].name) ||
               node.children[1].name.equals(GRAMMAR_BOOL_EXPR)){
      // bool assignment
    } else if (Arrays.asList(ID_TOKENS).contains(node.children[1].name)){
      // variable copy by reference
    } else if (TYPE_STRING_TOKEN.equals(node.children[1].name)){
      // string assignment
    }
  }
}

class Program extends code_generation{
  final static int MAX_CODE_SIZE = 256;
  short[] code = new short[MAX_CODE_SIZE];
  int code_stack_pos = 0;
  int heap_pos = MAX_CODE_SIZE - 1;
  Temp_data[] back_patch_data;
  int back_patch_count = 0;

  Program(AST_tree AST){
    back_patch_data = new Temp_data[AST.total_variables];
  }

  void add_instruction(short instruction){
    code[code_stack_pos] = instruction;
    code_stack_pos++;
  }

  void load_accumulator(short constant){
    add_instruction(LOAD_ACCUMULATOR_CONSTANT);
    add_instruction(constant);
  }

  void load_accumulator(Temp_data data){
    add_instruction(LOAD_ACCUMULATOR_MEMORY);
    add_instruction(data.name);
    add_instruction((short)0x00);
  }

  void store_accumulator(Temp_data data){
    add_instruction(STORE_ACCUMULATOR);
    add_instruction(data.name);
    add_instruction((short) 0x00);
  }
  void store_accumulator(short name){
    add_instruction(STORE_ACCUMULATOR);
    add_instruction(name);
    add_instruction((short) 0x00);
  }

  Temp_data new_temp_data(String var, int scope){
    back_patch_data[back_patch_count] = new Temp_data(var, scope);
    back_patch_count++;
    return back_patch_data[back_patch_count - 1];
  }


  short find_temp_data(String desired_var, int scope){
    for (Temp_data temp :
            back_patch_data) {
      if (temp.var.equals(desired_var) && temp.scope == scope) return temp.name;
    }
    return 0;
  }

  void print_code_hex(){
    System.out.println();
    System.out.println("HEX CODE:");
    int counter = 0;
    for (short byt: code) {
      if (counter >= 8){
        System.out.println();
        counter = 0;
      }
      System.out.print(String.format("%02x", byt) + " ");
      counter++;
    }
    System.out.println();
  }

  void print_code_hex_human_readable(){
    System.out.println();
    System.out.println("Human Readable Hex:");
    int counter = 0;
    int line_num = 0;
    System.out.print(String.format("%02x", line_num) + " : ");
    for (short byt: code) {
      if (counter >= 8){
        System.out.println();
        counter = 0;
        line_num += 8;
        System.out.print(String.format("%02x", line_num) + " : ");
      }
      System.out.print(String.format("%02x", byt) + " ");
      counter++;
    }
    System.out.println();
  }
}

class Temp_data{
  static short next_new_temp = 0x100; // anything past 0xFF is a temp variable as >= 0x100 is invalid output
  short name;
  String var;
  int scope;
  short address;

  Temp_data(String var, int scope) {
    this.name = next_new_temp;
    next_new_temp++;
    this.var = var;
    this.scope = scope;
  }
}
