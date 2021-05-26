import java.util.Arrays;
import java.util.List;

public class code_generation extends compiler{
  final static short LOAD_ACCUMULATOR_CONSTANT = 0xA9;
  final static short LOAD_ACCUMULATOR_MEMORY = 0xAD;
  final static short STORE_ACCUMULATOR = 0x8D;
  final static short ADD_WITH_CARRY = 0x6D;
  final static short LOAD_X_CONSTANT = 0xA2;
  final static short LOAD_X_MEMORY = 0xAE;
  final static short COMPARE = 0xEC;
  final static short BRANCH = 0xD0;

  static void generate_code(AST_tree AST, boolean verbose_mode){
    Program program = new Program(AST);
    generate_code_for_layer(AST.root, program);
    // end of code break
    program.add_instruction((short) 0x00);
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

  static void back_patch(Program program){
    int end_of_code = program.code_stack_pos;
    for (Temp_data data : program.back_patch_data) {
      if (!data.jump) {
        data.address = (short) program.code_stack_pos;
        program.code_stack_pos++;
      }
    }
    for (int i = 0; i < end_of_code; i++) {
      if (program.code[i] > 0xFF){
        for (Temp_data data: program.back_patch_data){
          if (program.code[i] == data.name) program.code[i] = data.address;
        }
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
  }

  static void var_decl(AST_node node, Program program){
    program.load_accumulator_constant((short)0x00);
    program.store_accumulator(program.new_temp_data(node.children[1].name, node.find_scope()).name);
  }

  static void assignment(AST_node node, Program program){
    if (Arrays.asList(DIGIT_TOKENS).contains(node.children[1].name)){
      // int assignment
      if (node.children[2] == null){
        //single assignment
        program.load_accumulator_constant((short) Arrays.asList(DIGIT_TOKENS).indexOf(node.children[1].name));
        program.store_accumulator(program.find_temp_data(node.children[0].name, node).name);
      } else {
        // addition then assignment
        String[] int_expr = new String[node.children.length / 2];
        int temp = 0;
        for (int i = 1; i < node.children.length; i++) {
          if (node.children[i] != null && !node.children[i].name.equals(ADDITION_OP_TOKEN)){
            int_expr[temp] = node.children[i].name;
            temp++;
          }
        }
        int_expression(int_expr, program, node);
      }
    } else if (Arrays.asList(BOOL_VALS).contains(node.children[1].name)){
      // bool assignment
      if (node.children[1].name.equals(BOOL_VALS[0])){
        program.load_accumulator_constant((short) 0x00);
      } else {
        program.load_accumulator_constant((short) 0x01);
      }
      program.store_accumulator(program.find_temp_data(node.children[0].name, node).name);
    } else if (node.children[1].name.equals(GRAMMAR_BOOL_EXPR)) {
      // bool expression then assignment
      bool_expression(program, node.children[1]);
      if (node.children[1].children[1].name.equals(EQUALITY_TOKEN)){
        program.load_accumulator_constant((short) 0x00);
      } else {
        program.load_accumulator_constant((short) 0x01);
      }
      Temp_data jump = program.branch();
      if (node.children[1].children[1].name.equals(EQUALITY_TOKEN)){
        program.load_accumulator_constant((short) 0x01);
      } else {
        program.load_accumulator_constant((short) 0x00);
      }
      jump.address = 0x02;
      program.store_accumulator(program.find_temp_data(node.children[0].name, node).name);
    } else if (Arrays.asList(ID_TOKENS).contains(node.children[1].name)){
      // variable copy by reference
      // load data from variable
      program.load_accumulator_memory(program.find_temp_data(node.children[1].name, node));
      program.store_accumulator(program.find_temp_data(node.children[0].name, node).name);
    } else if (TYPE_STRING_TOKEN.equals(node.children[1].name)){
      // string assignment
      program.load_accumulator_constant(program.get_heap_string(node.children[1].children[0].name).address);
      program.store_accumulator(program.find_temp_data(node.children[0].name, node).name);
    }
  }

  static void bool_expression(Program program, AST_node node){
    // expression must always be 3 long
    if (Arrays.asList(ID_TOKENS).contains(node.children[0].name)){
      program.load_x_memory(program.find_temp_data(node.children[0].name, node).name);
    } else if (node.children[0].name.equals(TYPE_STRING_TOKEN)){
      program.load_x_memory(program.get_heap_string(node.children[0].children[0].name).address);
    } else {
      if (node.children[0].name.equals(BOOL_VALS[0])){
        program.load_x_constant((short) 0x00);
      } else if (node.children[0].name.equals(BOOL_VALS[1])) {
        program.load_x_constant((short) 0x01);
      } else {
        program.load_x_constant((short) Arrays.asList(DIGIT_TOKENS).indexOf(node.children[0].name));
      }
    }

    if (Arrays.asList(ID_TOKENS).contains(node.children[2].name)){
      program.compare(program.find_temp_data(node.children[2].name, node).name);
    } else if (node.children[2].name.equals(TYPE_STRING_TOKEN)){
      program.compare(program.get_heap_string(node.children[2].children[0].name).address);
    } else {
      if (node.children[2].name.equals(BOOL_VALS[0])){
        program.compare(program.get_heap_simple_data(BOOL_VALS[0]).address);
      } else if (node.children[2].name.equals(BOOL_VALS[1])) {
        program.compare(program.get_heap_simple_data(BOOL_VALS[1]).address);
      } else {
        program.compare(program.get_heap_simple_data(node.children[2].name).address);
      }
    }
  }

  static void int_expression(String[] expression, Program program, AST_node node) {
    List<String> digit_tokens = Arrays.asList(DIGIT_TOKENS);
    program.load_accumulator_constant((short) digit_tokens.indexOf(expression[0]));

    for (int i = 1; i < expression.length; i++) {
      if (expression[i] != null) program.add_to_accumulator(expression[i], node);
    }
  }
}

class Program extends code_generation{
  final static int MAX_CODE_SIZE = 256;
  short[] code = new short[MAX_CODE_SIZE];
  int code_stack_pos = 0;
  int heap_pos = MAX_CODE_SIZE - 1;
  Temp_data[] back_patch_data;
  Heap_data[] heap_data;
  int back_patch_count = 0;
  AST_tree AST;

  Program(AST_tree AST){
    back_patch_data = new Temp_data[AST.total_variables];
    heap_data = new Heap_data[10];
    this.AST = AST;
  }

  void add_instruction(short instruction){
    code[code_stack_pos] = instruction;
    code_stack_pos++;
  }

  void load_accumulator_constant(short constant){
    add_instruction(LOAD_ACCUMULATOR_CONSTANT);
    add_instruction(constant);
  }

  void load_accumulator_memory(Temp_data data){
    add_instruction(LOAD_ACCUMULATOR_MEMORY);
    add_instruction(data.name);
    add_instruction((short)0x00);
  }

  void load_accumulator_memory(short address){
    add_instruction(LOAD_ACCUMULATOR_MEMORY);
    add_instruction(address);
    add_instruction((short)0x00);
  }

  void store_accumulator(short name){
    add_instruction(STORE_ACCUMULATOR);
    add_instruction(name);
    add_instruction((short) 0x00);
  }

  void add_to_accumulator(String int_token , AST_node node){
    add_instruction(ADD_WITH_CARRY);
    if (Arrays.asList(DIGIT_TOKENS).contains(int_token)) {
      add_instruction(get_heap_simple_data(int_token).address);
    } else {
      add_instruction(find_temp_data(int_token, node).name);
    }
    add_instruction((short) 0x00);
  }

  void load_x_constant(short constant){
    add_instruction(LOAD_X_CONSTANT);
    add_instruction(constant);
  }

  void load_x_memory(short address){
    add_instruction(LOAD_X_MEMORY);
    add_instruction(address);
    add_instruction((short) 0x00);
  }

  void compare(short address){
    // compares address to x register
    // sets z = 1 if equal
    add_instruction(COMPARE);
    add_instruction(address);
    add_instruction((short) 0x00);
  }

  Temp_data branch(){
    // jumps a number of bytes if z is = 0
    add_instruction(BRANCH);
    Temp_data jump = new_jump();
    add_instruction(jump.name);
    return jump;
  }

  Temp_data new_temp_data(String var, int scope){
    back_patch_data[back_patch_count] = new Temp_data(var, scope);
    back_patch_count++;
    return back_patch_data[back_patch_count - 1];
  }

  Temp_data new_jump(){
    back_patch_data[back_patch_count] = new Temp_data(true);
    back_patch_count++;
    return back_patch_data[back_patch_count - 1];
  }


  Temp_data find_temp_data(String desired_var, AST_node node){
    int scope = node.find_scope();
    for (Temp_data temp :
            back_patch_data) {
      if (temp.var.equals(desired_var) && temp.scope == scope) return temp;
    }
    if (node.parent != null) {
      return find_temp_data(desired_var, node.parent);
    } else {
      return null;
    }
  }

  Heap_data get_heap_simple_data(String byte_of_data){
    int first_empty = -1;
    // check if data is already in heap
    for (int i = 0; i < heap_data.length; i++) {
      if (heap_data[i] != null){
        if (heap_data[i].var.equals(byte_of_data) &&
                !heap_data[i].is_string){
          return heap_data[i];
        }
      } else {
        first_empty = i;
        break;
      }
    }
    // double array if full
    if (first_empty == -1){
      Heap_data[] new_heap_data = new Heap_data[heap_data.length * 2];
      System.arraycopy(heap_data, 0, new_heap_data, 0, heap_data.length);
      first_empty = heap_data.length;
      heap_data = new_heap_data;
    }
    // add data if does not exist
    List<String> temp = Arrays.asList(DIGIT_TOKENS);
    if (temp.contains(byte_of_data)) {
      code[heap_pos] = (short) temp.indexOf(byte_of_data);
    } else if (BOOL_VALS[0].equals(byte_of_data)){
      code[heap_pos] = (short) 0x0;
    } else if (BOOL_VALS[1].equals(byte_of_data)){
      code[heap_pos] = (short) 0x1;
    }
    heap_data[first_empty] = new Heap_data(byte_of_data, (short) heap_pos, false);
    heap_pos--;
    return heap_data[first_empty];
  }

  Heap_data get_heap_string(String string){
    int first_empty = -1;
    // check if data is already in heap
    for (int i = 0; i < heap_data.length; i++) {
      if (heap_data[i] != null){
        if (heap_data[i].var.equals(string) &&
                heap_data[i].is_string){
          return heap_data[i];
        }
      } else {
        first_empty = i;
        break;
      }
    }
    // double array if full
    if (first_empty == -1){
      Heap_data[] new_heap_data = new Heap_data[heap_data.length * 2];
      System.arraycopy(heap_data, 0, new_heap_data, 0, heap_data.length);
      first_empty = heap_data.length;
      heap_data = new_heap_data;
    }
    // add data if does not exist
    code[heap_pos] = 0x00;
    heap_pos--;
    for (int i = string.length() - 1; i >= 0 ; i--) {
      code[heap_pos] = (short) string.charAt(i);
      heap_pos--;
    }
    heap_data[first_empty] = new Heap_data(string, (short) (heap_pos + 1), true);
    return heap_data[first_empty];
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

class Heap_data{
  String var;
  short address;
  boolean is_string;

  public Heap_data(String var, short address, boolean is_string) {
    this.var = var;
    this.address = address;
    this.is_string = is_string;
  }

  Heap_data() {

  }
}

class Temp_data extends Heap_data{
  static short next_new_temp = 0x100; // anything past 0xFF is a temp variable as >= 0x100 is invalid output
  short name;
  int scope;
  boolean jump;

  Temp_data(String var, int scope) {
    this.name = next_new_temp;
    next_new_temp++;
    this.var = var;
    this.scope = scope;
  }

  Temp_data(boolean jump) {
    // for jumps this.address will be the numb of bytes to jump
    this.name = next_new_temp;
    next_new_temp++;
    this.jump = jump;
  }
}
