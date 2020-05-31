import java.lang.*;
import java.util.*;
import java.io.*;

public class CarTracker{
  public static void main(String[] args) throws FileNotFoundException, IOException {

    CarStructure carList;
    String vin = null;
    String make = null;
    String model = null;
    int price = -1;
    int mileage = -1;
    String color = null;
    int choice = -1;

    // READ IN THE DATA FROM THE FILE AND STORE IT IN AN ARRAY LIST
    //#########################################################
    ArrayList<String> stringArray = new ArrayList<String>();
    FileReader theReader = new FileReader("cars.txt");
    BufferedReader theBuffer = new BufferedReader(theReader);
    String s;
    while((s = theBuffer.readLine()) != null){
      stringArray.add(s);
    }
    theReader.close();

    // INSTANCIATE THE CAR LIST AND ADD THE CARS TO THE LIST
    //#########################################################
    carList = new CarStructure(stringArray.size());
    for(String S : stringArray){
      if(S.charAt(0) != '#'){
        Car theCar = new Car(S);
        carList.insert(theCar);
      }
    }

    // BEGIN USER INTERACTION COLLECTION
    //#########################################################
    Scanner scanChoice = new Scanner(System.in);

    do{
      System.out.println("\nWhat would you like to do? ");
      System.out.println("------------------------------------------------------------");
      System.out.println("1 - add a car to the list.");
      System.out.println("2 - update a car in the list.");
      System.out.println("3 - remove a specific car from consideration.");
      System.out.println("4 - retrieve the lowest priced car.");
      System.out.println("5 - retrieve the lowest mileage car.");
      System.out.println("6 - retrieve the lowest price car by make and model.");
      System.out.println("7 - retrieve the lowest mileage car by make and model.");
      System.out.println("8 - end program.\n");

      if(scanChoice.hasNextInt())
        choice = scanChoice.nextInt();

      switch(choice){
        case 1: // add a car to the list
          System.out.println("\nWhat is the VIN?");
          vin = scanChoice.next();
          System.out.println("\nWhat is the Make?");
          make = scanChoice.next();
          System.out.println("\nWhat is the Model?");
          model = scanChoice.next();
          System.out.println("\nWhat is the Price?");
          price = scanChoice.nextInt();
          System.out.println("\nWhat is the Mileage?");
          mileage = scanChoice.nextInt();
          System.out.println("\nWhat is the Color?");
          color = scanChoice.next();

          Car newCar = new Car(vin, make, model, price, mileage, color);
          carList.insert(newCar);
          System.out.println("\nCar added - "+newCar);
          break;

        case 2: // update a car in the list
          int editNo = -1;
          System.out.println("\nWhat is the VIN?");
          vin = scanChoice.next();

          do{
            System.out.println("\nWhat would you like to edit?");
            System.out.println("----------------------------------");
            System.out.println("1 - update the price.");
            System.out.println("2 - update the mileage.");
            System.out.println("3 - update the color.");
            editNo = scanChoice.nextInt();
            if(editNo == 1){
              System.out.println("\nWhat is the price?");
              price = scanChoice.nextInt();
              carList.update(vin, editNo, price, color);
            } else if(editNo == 2){
              System.out.println("\nWhat is the mileage?");
              mileage = scanChoice.nextInt();
              carList.update(vin, editNo, mileage, color);
            } else if(editNo == 3){
              System.out.println("\nWhat is the color?");
              color = scanChoice.next();
              carList.update(vin, editNo, price, color);
            } else{
              editNo = -1;
              System.out.println("\nPlease enter an integer between 1 - 3.");
            }
          } while(editNo == -1);
          System.out.println("\nUpdated - "+carList.getCar(vin));
          break;

        case 3: // remove a car from the list
          System.out.println("\nWhat is the VIN?");
          vin = scanChoice.next();
          System.out.println("Removed - "+carList.remove(vin));
          break;

        case 4: // get lowest priced car
          System.out.println(carList.retrieveMin(choice, model));
          break;

        case 5: // get lowest mileage car
          System.out.println(carList.retrieveMin(choice, model));
          break;

        case 6: // get lowest priced of a specific model
          System.out.println("\nWhat is the Make?");
          make = scanChoice.next();
          System.out.println("\nWhat is the Model?");
          model = scanChoice.next();
          System.out.println(carList.retrieveMin(choice, model));
          break;

        case 7: // get lowest mileage of a specific model
          System.out.println("\nWhat is the Make?");
          make = scanChoice.next();
          System.out.println("\nWhat is the Model?");
          model = scanChoice.next();
          System.out.println(carList.retrieveMin(choice, model));
          break;

        case 8:
          choice = 8;
          break;

        default:
          System.out.println("Please enter a number between 1 and 8.");
      }
    } while(choice != 8);
    System.out.println(carList);
  }
}
