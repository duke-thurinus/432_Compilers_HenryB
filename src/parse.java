public class parse {
  static void parser(Token_stream token_stream){

  }
}

class CST {
  CST_node root = null;
  CST_node current = null;

    void add_node(String name){
    if (root == null){
      //make root
      root = new CST_node(name);
      current = root;
    } else {
      //new node
      for (int i=0; i < current.children.length; i++){
        if (current.children[i] == null){
          current.children[i] = new CST_node(name);
          current.children[i].parent = current;
          current = current.children[i];
          break;
        }
      }
    }
  }

  void move_up_to_parent(){
      if (current.parent != null){
        current = current.parent;
      }
  }

}

class CST_node {
  String name;
  CST_node parent = null;
  CST_node[] children;

  CST_node(String name){
    this.name = name;
    children = new CST_node[5];
  }

  CST_node(String name, CST_node parent){
    this.name = name;
    this.parent = parent;
    children = new CST_node[5];
  }
}
