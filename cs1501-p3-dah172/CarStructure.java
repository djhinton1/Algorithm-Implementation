import java.lang.*;
import java.util.*;

public class CarStructure{

  private IndexMinPQ allByMiles;
  private IndexMinPQ allByPrice;
  private TreeMap<String, IndexMinPQ> modelsByMiles;
  private TreeMap<String, IndexMinPQ> modelsByPrice;
  private int size = 0;

  public CarStructure(int size){
    this.size = size;
    allByMiles = new IndexMinPQ(size, "BY_MILES");
    allByPrice = new IndexMinPQ(size, "BY_PRICE");
    modelsByMiles = new TreeMap<String, IndexMinPQ>();
    modelsByPrice = new TreeMap<String, IndexMinPQ>();
  }

  public void insert(Car theCar){
    allByMiles.insert(theCar);
    allByPrice.insert(theCar);

    if(modelsByMiles.containsKey(theCar.getModel())){
      modelsByMiles.get(theCar.getModel()).insert(theCar);
    } else{
      modelsByMiles.put(theCar.getModel(), new IndexMinPQ(size, "BY_MILES"));
      modelsByMiles.get(theCar.getModel()).insert(theCar);
    }

    if(modelsByPrice.containsKey(theCar.getModel())){
      modelsByPrice.get(theCar.getModel()).insert(theCar);
    } else{
      modelsByPrice.put(theCar.getModel(), new IndexMinPQ(size, "BY_PRICE"));
      modelsByPrice.get(theCar.getModel()).insert(theCar);
    }
  }

  public Car remove(String vin){
    int a = allByMiles.getIndexFromVin(vin);
    int b = allByPrice.getIndexFromVin(vin);
    int c = modelsByMiles.get(allByMiles.carOf(a).getModel()).getIndexFromVin(vin);
    int d = modelsByPrice.get(allByMiles.carOf(a).getModel()).getIndexFromVin(vin);

    Car theRemovedCar = allByMiles.carOf(a);

    modelsByMiles.get(allByMiles.carOf(a).getModel()).delete(c);
    modelsByPrice.get(allByMiles.carOf(a).getModel()).delete(d);
    allByMiles.delete(a);
    allByPrice.delete(b);

    return theRemovedCar;
  }

  public Car retrieveMin(int whichPQ, String model){
    switch(whichPQ){
      case 5:
        return allByMiles.minCar();
      case 4:
        return allByPrice.minCar();
      case 7:
        return modelsByMiles.get(model).minCar();
      case 6:
        return modelsByPrice.get(model).minCar();
    }
    return null;
  }

  public Car update(String vin, int attribute, int value, String color){
    Car replacement = null;
    switch(attribute){
      case 1: // change the price
        replacement = this.remove(vin);
        replacement.setPrice(value);
        this.insert(replacement);
        break;
      case 2: // change the mileage
        replacement = remove(vin);
        replacement.setMiles(value);
        this.insert(replacement);
        break;
      case 3: // change the color
        replacement = remove(vin);
        replacement.setColor(color);
        this.insert(replacement);
        break;
    }
    return replacement;
    }

    public String toString(){
      return allByPrice.toString();
    }

    public Car getCar(String vin){
      int index = allByMiles.getIndexFromVin(vin);
      return allByMiles.carOf(index);
    }



// TESTING
//###############################################################################
  /*public static void main(String[] args) {
      // insert a bunch of strings
      //String[] strings = { "it", "was", "the", "best", "of", "times", "it", "was", "the", "worst" };

      //testing for cars, specific to project 3
      Car a = new Car("U123","Ford","Fiesta",12000,300,"Green");
      Car b = new Car("U286","Toyota","Prius",140000,10,"Blue");
      Car c = new Car("U357","Chevorlet","Camero",67000,27384,"Red");
      Car d = new Car("U799","Toyota","Prius",32000,100000,"Orange");
      Car e = new Car("U002","Honda","Civic",24000,23,"Purple");

      Car[] cars = {a, b, c, d, e};

      CarStructure theList = new CarStructure(5);
      for (Car C : cars) {
          theList.insert(C);
      }

      /*System.out.println("This is in the allPrice PQ");
      System.out.println("--------------------------------------------");
      System.out.println(theList.allByPrice);
      System.out.println("\n");
      System.out.println("This is in the allMiles PQ");
      System.out.println("--------------------------------------------");
      System.out.println(theList.allByMiles);
      System.out.println("\n");
      System.out.println("This is in the ModelsPrice TreeMap");
      System.out.println("--------------------------------------------");
      for(Map.Entry<String,IndexMinPQ> P :theList.modelsByPrice.entrySet()){
        System.out.println("NewPQ \n");
        System.out.println(P.getValue());
      }
      System.out.println("\n");
      System.out.println("This is in the ModelsMiles TreeMap");
      System.out.println("--------------------------------------------");
      for(Map.Entry<String,IndexMinPQ> P :theList.modelsByMiles.entrySet()){
        System.out.println("NewPQ \n");
        System.out.println(P.getValue());
      }
      System.out.println("\n");
      System.out.println("Min Mile Vehicle - "+theList.retrieveMin(1, "Prius"));
      System.out.println("Min Price Vehicle - "+theList.retrieveMin(2, "Prius"));
      System.out.println("This vehicle was removed: "+ theList.remove("U357"));
      System.out.println("This is min mileage Prius: "+ theList.retrieveMin(3, "Prius"));
      System.out.println("This is min price Prius: "+ theList.retrieveMin(4, "Prius"));
      System.out.println("This car changed mileage: "+ theList.update("U799",2,0,"green"));
      System.out.println("Min Mile Vehicle - "+theList.retrieveMin(1, "Prius"));
  }*/
}
