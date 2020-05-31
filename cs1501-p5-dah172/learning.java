public class learning{
  public static void main(String[] args){
    int a = 318;
    System.out.println(String.format("%8s",Integer.toBinaryString(a & 0xFF)).replace(' ','0'));
  }
}
