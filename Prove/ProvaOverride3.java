
class Veicolo {
    //Creazione di una classe genitore. definire un metodo
    void run() {
        System.out.println("Il veicolo è in marcia");
    }
}

class Bike extends Veicolo {
    public static void main(String args[]) {
        Bike obj = new Bike();
        obj.run(); // stampa riga 5
    }
}

class Bike2 extends Veicolo {
    // @ override
    // non serve scrivere override perchè nello stesso file
    void run() { 
        System.out.println("La bici sta correndo in sicurezza");
    } // metodo della classe genitore

    public static void main(String args[]) {
        Bike2 obj = new Bike2(); // crea oggetto
        obj.run(); // stamap riga 20
    }
}