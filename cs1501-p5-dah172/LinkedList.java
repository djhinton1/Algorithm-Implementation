
class LinkedList{
  private Node _head; //this is the head of the likned LinkedList
  static int count;
  //Linked List constructor
  public LinkedList(){
    _head = null;
  }

  static class Node{
    HeftyInteger data;
    Node next; //this is reference to other node

    //node constructor;
    Node(HeftyInteger d){
      data = d;
      next = null;
    }
  }

  public void add(HeftyInteger hi){
    Node n = new Node(hi);
    if(_head == null){
      _head = n;
      count++;
      return;
    }
    n.next = _head;
    _head = n;
    count++;
    return;
  }

  public HeftyInteger remove(){
    HeftyInteger value = _head.data;
    _head = _head.next;
    count--;
    return value;
  }

  public boolean isEmpty(){
    return count == 0;
  }
}
