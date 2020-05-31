/******************************************************************************
 *  Compilation:  javac IndexMinPQ.java
 *  Execution:    java IndexMinPQ
 *  Dependencies: StdOut.java
 *
 *  Minimum-oriented indexed PQ implementation using a binary heap.
 *
 ******************************************************************************/



import java.util.*;
import java.util.NoSuchElementException;

/**
 *  The {@code IndexMinPQ} class represents an indexed priority queue of generic cars.
 *  It supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 *  operations, along with <em>delete</em> and <em>change-the-car</em>
 *  methods. In order to let the client refer to cars on the priority queue,
 *  an integer between {@code 0} and {@code maxN - 1}
 *  is associated with each car—the client uses this integer to specify
 *  which car to delete or change.
 *  It also supports methods for peeking at the minimum car,
 *  testing if the priority queue is empty, and iterating through
 *  the cars.
 *  <p>
 *  This implementation uses a binary heap along with an array to associate
 *  cars with integers in the given range.
 *  The <em>insert</em>, <em>delete-the-minimum</em>, <em>delete</em>,
 *  <em>change-car</em>, <em>decrease-car</em>, and <em>increase-car</em>
 *  operations take &Theta;(log <em>n</em>) time in the worst case,
 *  where <em>n</em> is the number of elements in the priority queue.
 *  Construction takes time proportional to the specified capacity.
 *  <p>
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 *  @param <Car> the generic type of car on this priority queue
 */
public class IndexMinPQ implements Iterable<Integer> {
    private int maxN;        // maximum number of elements on PQ
    private int n;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Car[] cars;      // cars[i] = priority of i
    private String method = "BY_PRICE";   // method of comparison, can change to be user specified
    private Hashtable<String, Integer> indexVin; // stores the index with a VIN key
    private int insertNum = 0;
    private ArrayList<Integer> lastRemoved = new ArrayList<Integer>();

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     * @param  maxN the cars on this priority queue are index from {@code 0}
     *         {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN < 0}
     */
    public IndexMinPQ(int maxN, String method) {
        if (maxN < 0) throw new IllegalArgumentException();
        this.indexVin = new Hashtable<String, Integer>();
        this.maxN = maxN;
        this.method = method;
        n = 0;
        cars = new Car[maxN + 1];    // make this of length maxN??
        pq   = new int[maxN + 1];
        qp   = new int[maxN + 1];                   // make this of length maxN??
        for (int i = 0; i <= maxN; i++)
            qp[i] = -1;
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Is {@code i} an index on this priority queue?
     *
     * @param  i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     */
    public boolean contains(int i) {
        validateIndex(i);
        return qp[i] != -1;
    }

    /**
     * Returns the number of cars on this priority queue.
     *
     * @return the number of cars on this priority queue
     */
    public int size() {
        return n;
    }

    /**
     * Associates car with index {@code i}.
     *
     * @param  i an index
     * @param  car the car to associate with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if there already is an item associated
     *         with index {@code i}
     */
    public void insert(Car car) {
        int i;
        if(!lastRemoved.isEmpty()){
          i = lastRemoved.remove(0);
          System.out.println("we set i to last removed");
        } else{
            i = insertNum;
            insertNum++;
        }
        validateIndex(i);
        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        if(i > pq.length-2){
          int theLength = qp.length;
          pq = Arrays.copyOf(pq, pq.length * 2);
          qp = Arrays.copyOf(qp, qp.length * 2);
          for(int x = theLength; x < qp.length; x++){
            qp[x] = -1;
          }
          cars = Arrays.copyOf(cars, cars.length * 2);
        }
        n++;
        qp[i] = n;
        pq[n] = i;
        cars[i] = car;
        swim(n);
        indexVin.put(cars[i].getVin(), i);
    }

    /**
     * Returns an index associated with a minimum car.
     *
     * @return an index associated with a minimum car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int minIndex() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    /**
     * Returns a minimum car.
     *
     * @return a minimum car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Car minCar() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        return cars[pq[1]];
    }

    /**
     * Removes a minimum car and returns its associated index.
     * @return an index associated with a minimum car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int delMin() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        int min = pq[1];
        indexVin.remove(cars[min].getVin());
        lastRemoved.add(qp[min]);
        exch(1, n--);
        sink(1);
        assert min == pq[n+1];
        qp[min] = -1;        // delete
        cars[min] = null;    // to help with garbage collection
        pq[n+1] = -1;        // not needed
        return min;
    }

    /**
     * Returns the car associated with index {@code i}.
     *
     * @param  i the index of the car to return
     * @return the car associated with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no car is associated with index {@code i}
     */
    public Car carOf(int i) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        else return cars[i];
    }

    /**
     * Change the car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the car to change
     * @param  car change the car associated with index {@code i} to this car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no car is associated with index {@code i}
     */
    public void changeCar(int i, Car car) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        cars[i] = car;
        swim(qp[i]);
        sink(qp[i]);
    }

    /**
     * Change the car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the car to change
     * @param  car change the car associated with index {@code i} to this car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @deprecated Replaced by {@code changeCar(int, Car)}.
     */
    @Deprecated
    public void change(int i, Car car) {
        changeCar(i, car);
    }

    /**
     * Decrease the car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the car to decrease
     * @param  car decrease the car associated with index {@code i} to this car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if {@code car >= carOf(i)}
     * @throws NoSuchElementException no car is associated with index {@code i}
     */
    public void decreaseCar(int i, Car car) {
        int result = 0;
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if(method == "BY_PRICE"){
         result = cars[pq[i]].getPrice() - car.getPrice();
        }
        else{
         result = cars[pq[i]].getMiles() - car.getMiles();
        }
        if (result == 0)
            throw new IllegalArgumentException("Calling decreaseCar() with a car equal to the car in the priority queue");
        if (result < 0)
            throw new IllegalArgumentException("Calling decreaseCar() with a car strictly greater than the car in the priority queue");
        cars[i] = car;
        swim(qp[i]);
    }

    /**
     * Increase the car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the car to increase
     * @param  car increase the car associated with index {@code i} to this car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if {@code car <= carOf(i)}
     * @throws NoSuchElementException no car is associated with index {@code i}
     */
    public void increaseCar(int i, Car car) {
        int result = 0;
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if(method == "BY_PRICE"){
         result = cars[pq[i]].getPrice() - car.getPrice();
        }
        else{
         result = cars[pq[i]].getMiles() - car.getMiles();
        }
        if (result == 0)
            throw new IllegalArgumentException("Calling increaseCar() with a car equal to the car in the priority queue");
        if (result > 0)
            throw new IllegalArgumentException("Calling increaseCar() with a car strictly less than the car in the priority queue");
        cars[i] = car;
        sink(qp[i]);
    }

    /**
     * Remove the car associated with index {@code i}.
     *
     * @param  i the index of the car to remove
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no car is associated with index {@code i}
     */
    public void delete(int i) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        int index = qp[i];
        indexVin.remove(cars[i].getVin());
        lastRemoved.add(i);
        exch(index, n--);
        swim(index);
        sink(index);
        cars[i] = null;
        qp[i] = -1;
    }

    // throw an IllegalArgumentException if i is an invalid index
    private void validateIndex(int i) {
        if (i < 0) throw new IllegalArgumentException("index is negative: " + i);

    }

    public int getIndexFromVin(String vin){
      return indexVin.get(vin);
    }

    public String toString(){
      String toPrint = "";
      for (Car C:cars){
        if(C != null)
          toPrint = toPrint + C + "\n";
      }
      return toPrint;
    }


   /***************************************************************************
    * General helper functions.
    ***************************************************************************/
    private boolean greater(int i, int j) {
        if(method == "BY_PRICE"){
         return (cars[pq[i]].getPrice() - cars[pq[j]].getPrice()) > 0;
        }
        return (cars[pq[i]].getMiles() - cars[pq[j]].getMiles()) > 0;
    }

    private void exch(int i, int j) {
        int swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp[pq[i]] = i;
        qp[pq[j]] = j;
    }


   /***************************************************************************
    * Heap helper functions.
    ***************************************************************************/
    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }


   /***************************************************************************
    * Iterators.
    ***************************************************************************/

    /**
     * Returns an iterator that iterates over the cars on the
     * priority queue in ascending order.
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the cars in ascending order
     */
    public Iterator<Integer> iterator() { return new HeapIterator(); }

    private class HeapIterator implements Iterator<Integer> {
        // create a new pq
        private IndexMinPQ copy;

        // add all elements to copy of heap
        // takes linear time since already in heap order so no cars move
        public HeapIterator() {
            int backup = insertNum;
            copy = new IndexMinPQ(pq.length - 1, method);
            insertNum = 1;
            while(insertNum <= n){
              copy.insert(cars[pq[insertNum]]);
              insertNum++;
            }
            insertNum = backup;
        }

        public boolean hasNext()  { return !copy.isEmpty();                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }


    /**
     * Unit tests the {@code IndexMinPQ} data type.
     *
     * @param args the command-line arguments
     */
  /*  public static void main(String[] args) {
        // insert a bunch of strings
        //String[] strings = { "it", "was", "the", "best", "of", "times", "it", "was", "the", "worst" };

        //testing for cars, specific to project 3
        Car a = new Car("U123","Ford","Fiesta",12000,300,"Green");
        Car b = new Car("U286","Audi","R8",140000,12789,"Blue");
        Car c = new Car("U357","Chevorlet","Camero",67000,27384,"Red");
        Car d = new Car("U799","Toyota","Prius",32000,100000,"Orange");
        Car e = new Car("U002","Honda","Civic",24000,23,"Purple");

        ArrayList<Car> comparisons = new ArrayList<Car>();
        comparisons.add(a);
        comparisons.add(b);
        comparisons.add(c);
        comparisons.add(d);
        comparisons.add(e);


        IndexMinPQ pq = new IndexMinPQ(comparisons.size(), "BY_MILES");
        int p = 0;
        for (Car w : comparisons) {
            pq.insert(w);
            pq.indexVin.put(w.getVin(), p++);
        }

        Car f = new Car("U1997","Mitsubishi","Outlander",18000,1,"Purple");
        Car g = new Car("U2006","again","test",24000,23,"Purple");
        Car h = new Car("U2020","please","work",24000,23,"Purple");
        comparisons.add(f);
        comparisons.add(g);
        comparisons.add(h);
        pq.insert(f);
        pq.insert(g);
        pq.insert(h);



        // delete and print each car
        while (!pq.isEmpty()) {
            int i = pq.delMin();
            System.out.println();
            System.out.println(i + " " + comparisons.get(i));

            //int theCar = getIndexFromVin("U799");
            //System.out.println("This is the indexed car: "+ cars[theCar]);
        }
    }*/
}

/******************************************************************************
 *  Copyright 2002-2020, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
