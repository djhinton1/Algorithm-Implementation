import java.lang.*;
import java.util.*;

class Car{

  private String _vin;
  private String _make;
  private String _model;
  private int _price;
  private int _miles;
  private String _color;

  public Car(){
    _vin = null;
    _make = null;
    _model = null;
    _price = -1;
    _miles = -1;
    _color = null;
  }

  public Car(String information){
    String[] info = information.split(":");
    _vin = info[0];
    _make = info[1];
    _model = info[2];
    _price = Integer.parseInt(info[3]);
    _miles = Integer.parseInt(info[4]);
    _color = info[5];
  }

  public Car(String vin, String make, String model, int price, int miles, String color){
    _vin = vin;
    _make = make;
    _model = model;
    _price = price;
    _miles = miles;
    _color = color;
  }

  Comparator<Car> BY_PRICE =  new Comparator<Car>() {
        public int compare(Car c1, Car c2) {
            return c1.getPrice() - c2.getPrice();
        }
    };

  Comparator<Car> BY_MILES =  new Comparator<Car>() {
        public int compare(Car c1, Car c2) {
            return c1.getMiles() - c2.getMiles();
        }
    };

  public String toString(){
    return _vin+":"+_make+":"+_model+":"+_price+":"+_miles+":"+_color;
  }

  public String getVin(){
    return _vin;
  }

  public String getMake(){
    return _make;
  }

  public String getModel(){
    return _model;
  }

  public int getPrice(){
    return _price;
  }

  public int getMiles(){
    return _miles;
  }

  public String getColor(){
    return _color;
  }

  public void setVin(String vin){
    _vin = vin;
  }

  public void setMake(String make){
    _make = make;
  }

  public void setModel(String model){
    _model = model;
  }

  public void setPrice(int price){
    _price = price;
  }

  public void setMiles(int miles){
    _miles = miles;
  }

  public void setColor(String color){
    _color = color;
  }

  /*public static void main(String[] args){
    Car a = new Car("U123","Ford","Fiesta",12000,300,"Green");
    System.out.println(a.getPrice());
  }*/

}
