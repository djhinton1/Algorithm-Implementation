import java.io.*;
import java.lang.*;
import java.util.*;

public class ac_test{

  public static void main(String[] args) throws IOException{

    //WE START BY CONSTRUCTING THE LINKED LIST DLB TRIE STRUCTURE
    //AND THEN READ IN THE DATA FROM THE DICTIONARY
    LL theDictionary = new LL();
    FileReader theReader = new FileReader("dictionary.txt");
    BufferedReader theBuffer = new BufferedReader(theReader);
    String s;
    while((s = theBuffer.readLine()) != null){
      theDictionary.add(s);
    }
    theReader.close();

    //THEN WE CONSTRUCT ANOTHER TRIE STRUCTURE
    //I HAVE CHOSEN THIS DATA STRUCTURE TO REPRESENT THE USER HISTORY AS WELL
    //READ IN THE USER HISTORY IF THERE EXISTS ANY
    LL userHistory = new LL();
    File theHistory = new File("user_history.txt");
    if(theHistory.exists()){
      theReader = new FileReader(theHistory);
      theBuffer = new BufferedReader(theReader);
      while((s = theBuffer.readLine()) != null){
        userHistory.add(s);
      }
      theReader.close();
    }

    //STATE THE VARIABLES THAT WE WILL BE USING THROUGHOUT
    //THE MAIN METHOD
    char theCharacter;
    String predictedWord = null;
    long preSearch;
    long postSearch;
    long searchTime;
    String completedWord = null;
    String[] dicPredictions;
    String[] histPredictions;
    String[] dispPredictions = new String[5];
    int control = 0;
    String W = "";
    Scanner in = new Scanner(System.in);

    //DO-WHILE LOOP WILL REPEATEDLY ASK FOR USER INPUT, THIS IS AN OUTER LOOP
    //THAT RESTARTS WHEN A USER HAS COMPLETED A WORD WITH '$' OR HAS CHOSEN
    //A WORD FROM THE PREDICTIONS
    do{
      int index = 0;
      char[] theWord = new char[256];
      System.out.print("Enter the first character: ");
      s = in.nextLine();
      theCharacter = s.charAt(0);

      //ONCE A USER HAS STARTED ENTERING THE WORD, WE MUST PROVIDE PRIDICTIONS
      //FOR EVERY LETTER OBTAINED
      //THE WHILE LOOP RESTARTS WHEN A LETTER HAS BEEN ENTERED AND CONTAINS
      //THE GET PREDICTION REQUESTS AND THE SYSTEM OUTPUT PRINT LINES
      while(theCharacter != '$'){ //is the user has not finished a word
        if(theCharacter == '!'){ //if the user wants to exit
          control = 1;
          break; //break out of this while loop and exit the program
        }
        theWord[index] = theCharacter; //the letter will be added to an array of char
        W = W+s; //the same letter will be added to a String via concatenation
        preSearch = System.nanoTime(); //get system time for search comparison purposes
        dicPredictions = theDictionary.getPredictions(W); //obtain dictionary predictions
        histPredictions = userHistory.getPredictions(W); //obtain user history predictions
        postSearch = System.nanoTime(); //get system time for search comparison purposes
        searchTime = postSearch - preSearch; //calculate search time

        //HERE WE WANT TO DECIDE WHICH WORDS WILL BE OFFERED TO THE USER
        //AS PREDICTIONS FOR WHAT THEY ARE TYPING
        //THEY CAN EITHER COME FROM THE USER_HISTORY, OR FROM THE DICTIONARY
        //OR THE DISPLAYED PREDICTIONS WILL BE A COMBINATION OF BOTH
        int location = 0; //index location of the displayed prediction array
        for(String q:histPredictions){
          if(q != null){
            dispPredictions[location] = q; //fill the displayed predictions with the history first
            location++;
          }
        }
        for(String a:dicPredictions){
          if(location != 5){ //we only need to display 5 predictions
            for(int y = 0; y < location; y++){
              
            }
            dispPredictions[location] = a; //fill the displayed predictions with dictionary AFTER the history
            location++;
          }
        }

        //FORMATTING OF THE OUTPUT, TRIED TO MAKE IT LOOK AS CLOSELY AS I COULD
        //TO THE EXAMPLE OUTPUT FOR THE PROJECT
        double elapsedTime = (double)searchTime / 1_000_000_000.00; //convert search time to seconds
        System.out.print("(");
        System.out.printf("%1$.6f", elapsedTime);
        System.out.println(" s)");
        System.out.println("Predictions: ");
        if(dicPredictions == null){
          System.out.println("No Prediction");//should only happen if you enter something that is not aplhanumeric
        }
        else{
          System.out.print("(1) "+dispPredictions[0]);
          System.out.print("\t(2) "+dispPredictions[1]);
          System.out.print("\t(3) "+dispPredictions[2]);
          System.out.print("\t(4) "+dispPredictions[3]);
          System.out.println("\t(5) "+dispPredictions[4]+"\n");
        }

        //COLLECTING INPUT FROM THE USER AGAIN WITH SPECEFIC ATTENTION TO
        //INSERTING OF A NUMBER OR LETTER
        System.out.print("Enter the next character: ");
        s = in.nextLine();

        //IF THE USER ENTERS A NUMBER THEN WE NEED TO TO A COUPLE EXTRA THINGS
        if(isInteger(s)){ // will determine if user used a number or letter
          predictedWord = dispPredictions[Integer.parseInt(s)-1];
          System.out.println("\nWORD COMPLETED: "+predictedWord+"\n");
          index = 0;
          for(char t:dispPredictions[Integer.parseInt(s)-1].toCharArray()){
            if(Character.isLetter(t)){
              theWord[index] = t;
              index++;
            }
          }
          break; //breaks out of the while loop no need to ask for more input on this word
        }

        //IF WE REACH THESE LINES WITHIN THE WILE LOOP, THAT MEANS THAT
        //THE USER ENTERED A CHARACTER AND WE NEED TO ADD THAT LETTER TO
        //THE WORD WE ARE BUILDING AND OFFER MORE PREDICTIONS
        theCharacter = s.charAt(0);
        index++;
      } // end of while loop, if we pass this, that means we are done with the word

      //NOW WE NEED TO CHECK TO SEE IF THE USER WANTS TO END THE PROGRAM
      if(control == 1){
        break; //moves us out of the do-while loop. in otherwords, user is done.
      }

      //SINCE WE HAVE FINISHED WITH THE WORD AND HAVE NOT ENDED THE PROGRAM,
      //WE SHOULD ADD THE WORD TO THE USER_HISTORY AND RESET W;
      String userWord = new String(theWord);
      userHistory.add(userWord);
      W = "";
    }while(true); // end of do-while loop, if we pass this, user has ended the program

    System.out.println("Bye Bye!");

    //IF THE USER HAS ANY HISTORY, THEN WE NEED TO WRITE THAT HISTORY OUT
    //TO A DOCUMENT CALLED USER_HISTORY.TXT
    if(userHistory != null){
      userHistory.writeList("user_history.txt");
    }
  }

  //THIS WILL HELP TO DETERMINE IF THE INPUT IS A NUMBER OR A LETTER
  public static boolean isInteger(String s){
    try{
      Integer.parseInt(s);
    } catch(NumberFormatException e){
        return false;
    } catch(NullPointerException e){
        return false;
    }
    return true;
  }
}
