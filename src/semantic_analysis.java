public class semantic_analysis extends compiler {

  static void analysis(Syntax_tree AST, boolean verbose_mode) {
    System.out.println();
    AST.print_tree("AST");
  }
}