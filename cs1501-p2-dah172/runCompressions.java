import java.io.*;

public class runCompressions{
  public static void main(String args[]) throws IOException{
    Runtime run = Runtime.getRuntime();
    Process proc = run.exec("java MyLZW - n < frosty.jpg");
  }
}
