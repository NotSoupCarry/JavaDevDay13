class OuterClass {
    int x = 10;
  
    static class InnerClass {
      int y = 5;
    }
  }
  public class ProvaInnerClass2 {
    public static void main(String[] args) {
      OuterClass.InnerClass myInner = new OuterClass.InnerClass();
      System.out.println(myInner.y);
   }}
  