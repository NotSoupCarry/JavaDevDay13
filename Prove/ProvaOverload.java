public class ProvaOverload {

    // Metodo per sommare due numeri interi
    public int add(int a, int b) {
        return a + b; }
    // Metodo per sommare tre numeri interi
    public int add(int a, int b, int c) {
        return a + b + c;}
    // Metodo per sommare due numeri decimali
    public double add(double a, double b) {
        return a + b;}

    public static void main(String[] args) {
        ProvaOverload calc = new ProvaOverload();

        // Uso dell'overloading
        System.out.println("Somma di due interi: " + calc.add(2, 3));          // Chiama il primo metodo
        System.out.println("Somma di tre interi: " + calc.add(1, 2, 3));      // Chiama il secondo metodo
        System.out.println("Somma di due double: " + calc.add(2.5, 3.5));    // Chiama il terzo metodo
    }}