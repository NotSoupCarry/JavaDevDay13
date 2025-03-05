
class Vehicle { // super classe o classe genitore

  protected String brand = "Ford";

  public void honk() {

    System.out.println("Tuut, tuut!");
  }
}

// classe figlia che estente vehicle
class Car extends Vehicle {

  private String modelName = "Fiat";

  public static void main(String[] args) {

    Car myCar = new Car();

    myCar.honk();

    System.out.println(myCar.brand + " " + myCar.modelName);
  }

}