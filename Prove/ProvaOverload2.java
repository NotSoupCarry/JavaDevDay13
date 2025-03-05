public class ProvaOverload2 {

    // Metodo che accetta un nome come stringa
    public void sayHello(String name) {
        System.out.println("Ciao, " + name + "!");
    }

    // Metodo che accetta un numero e usa un saluto generico
    public void sayHello(int times) {
        for (int i = 0; i < times; i++) {
            System.out.println("Ciao! numero: " + i);
        }
    }

    // Metodo senza parametri
    public void sayHello() {
        System.out.println("Ciao, Mondo!");
    }

    public static void main(String[] args) {
        ProvaOverload2 greeting = new ProvaOverload2();
        // Esempio di chiamata dei metodi sovraccaricati
        greeting.sayHello(); // Chiama il metodo senza parametri
        greeting.sayHello("Mirko"); // Chiama il metodo con una stringa
        greeting.sayHello(3); // Chiama il metodo con un intero
    }
}