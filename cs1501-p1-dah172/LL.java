import java.io.*;
import java.lang.*;
import java.util.Scanner;

class LL{
  private Node _head; //this is the head of the likned LinkedList
  static int count;
  //Linked List constructor
  public LL(){
    _head = new Node();
  }

  static class Node{
    char _value;
    Node _child; //this is reference to other node
    Node _sibling; //this is reference to other node
    boolean _terminator;

    //node constructor;
    Node(char v){
      _value = v;
      _terminator = false;
      _child = null;
      _sibling = null;
    }

    //default node constructor
    Node(){
      _value = (char)'^';
      _terminator = false;
      _child = null;
      _sibling = null;
    }
  }

/*public static void main(String[] args){
  LL theDictionary = new LL();
  theDictionary.add("school");
  theDictionary.add("shoes");
  theDictionary.add("swing");
  theDictionary.add("skies");
  theDictionary.add("socks");
  theDictionary.add("bill");

  theDictionary.printList();
  System.out.println("these are the predictions for s: ");

  String[] predictions = getPredictions("sh");
  for(String s:predictions){
    System.out.println(s);
  }
}*/

  public void add(String s){
    Node checkThis;
    int status;
    char[] word = s.toCharArray();
    Node back = _head;
    String backOrgin;
    checkThis = _head._child;
    backOrgin = "child";
    for(char c:word){
      status = 0;
      while(status == 0){
        if(checkThis == null){
          checkThis = new Node(c);
          if(backOrgin == "child"){
            back._child = checkThis;
            //System.out.println("Added "+c+" as child");
          }
          else{
            back._sibling = checkThis;
            //System.out.println("Added "+c+" as sibling");
          }
          back = checkThis;
          checkThis = checkThis._child;
          backOrgin = "child";
          status = 1;
        }
        else{
          //does checkThis._value equal the character?
          if(checkThis._value == c){
            //if yes, then onto the next letter and the child
            //System.out.println("A path already exists for "+c);
            back = checkThis;
            checkThis = checkThis._child;
            backOrgin = "child";
            status = 1;
          }
          else{
            //if no, then we need to check the sibling (FOR THE SAME LETTER)
            back = checkThis;
            checkThis = checkThis._sibling;
            backOrgin = "sibling";
            //if the sibling does not exist, then we need to create one for this LETTER
            //THIS IS WHERE I WOULD HIT THE REPEAT BUTTON
          }
        }
      }
    }
    back._terminator = true;
  }

  public void printList(){
    char[] theString = new char[256];
    printNode(_head._child, theString, 0);
    System.out.println("\n");
  }

  public void printNode(Node n, char[] theString, int index){
    theString[index] = n._value;
    if(n._terminator == true){
      String word = new String(theString);
      System.out.println(word);
      if(n._child != null){
        printNode(n._child, theString.clone(), index+1);
      }
      if(n._sibling != null){
        printNode(n._sibling, theString.clone(), index);
      }
      return;
    }
    else{
      printNode(n._child, theString.clone(), index+1);
      if(n._sibling != null){
        printNode(n._sibling, theString.clone(), index);
      }
    }
  }

  public void writeList(String fileName) throws IOException{
    String word;
    int status = 0;
    File theFile = new File(fileName);
    theFile.createNewFile();
    BufferedWriter w = new BufferedWriter(new FileWriter(fileName, true));
    char[] theString = new char[256];
    writeNode(_head._child, theString, 0, w);
    w.close();
    return;
  }

  public void writeNode(Node n, char[] theString, int index, BufferedWriter w) throws IOException{
    if(index == 256){
      return;
    }
    theString[index] = n._value;
    if(n._terminator == true){
      String word = new String(theString);
      w.append(word + "\n");
      if(n._child != null){
        writeNode(n._child, theString.clone(), index+1, w);
      }
      if(n._sibling != null){
        writeNode(n._sibling, theString.clone(), index, w);
      }
      return;
    }
    else{
      writeNode(n._child, theString.clone(), index+1, w);
      if(n._sibling != null){
        writeNode(n._sibling, theString.clone(), index, w);
      }
    }
  }

  public Node getNode(String thePrefix){
    Node startingPoint = _head;
    char[] prefixChar = thePrefix.toCharArray();
    for(char c:prefixChar){
      if(startingPoint != null){
        startingPoint = findNode(startingPoint._child, c);
      }
    }
    return startingPoint;
  }

  public static Node findNode(Node startingPoint, char c){
    Node nodeViewing;
    Node result = null;
    if((nodeViewing = startingPoint) != null){
      //check to see if if matches what we are looking for
      if(nodeViewing._value == c){
        //System.out.println("we are in nodeViewing with value "+c);
        return nodeViewing;
      }
      else{
        if(nodeViewing._sibling == null){
          return null;
        }
        result = findNode(nodeViewing._sibling, c);
      }
    }
    return result;
  }

  public String[] getPredictions(String prefix){
    //System.out.println("this is the value of getpredictions first head child: "+_head._child._value);
    Node start = getNode(prefix);
    char[] predictChars = new char[256];
    String[] predictions = new String[5];
    int Status = 0;
    count = 0;
    if(start != null){
      if(start._terminator == true){
        predictions[0] = prefix;
        count++;
      }
    }
    if(start == null){
      return predictions;
    }
    if(start._child != null){
      predictions = addPrediction(start._child, predictChars, 0, predictions);
      int i = 0;
      for(String s:predictions){
        if(s != null){
          if(s == prefix){
            i++;
          }
          else{
            predictions[i] = prefix+s;
            i++;
          }
        }
      }
    }
    else{
      return null; //otherwise thhere are no predictions from the set
    }
    //System.out.println("this is the value of getpredictions second head child: "+_head._child._value);
    return predictions;
  }

  public static String[] addPrediction(Node n, char[] predictChars, int index, String[] predictions){
    if(count == 5){
      return predictions;
    }
    //otherwise add value to char array
    predictChars[index] = n._value;
    //System.out.println("we are in addPrediction with n_value =  "+n._value);
    //if this is the end of a word then we have found a prediction!
    if(n._terminator == true){
      //System.out.println("did we terminate: "+n._terminator);
      //System.out.println("value of count: "+count);
      String word = new String(predictChars);
      predictions[count] = word;
      count++;
      if(n._child != null){
        addPrediction(n._child, predictChars.clone(), index+1, predictions);
      }
      if(n._sibling != null){
        addPrediction(n._sibling, predictChars.clone(), index, predictions);
      }
      return predictions;
    }
    else{
      addPrediction(n._child, predictChars.clone(), index+1, predictions);
      if(n._sibling != null){
        addPrediction(n._sibling, predictChars.clone(), index, predictions);
      }
    }
    return predictions;
  }

}
