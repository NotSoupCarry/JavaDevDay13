public class ProvaOverride {
    public static void main(String[] args) {
        Over over = new Over(); 
        over.saluta(); // stampa ciao

        Pippo pippo = new Pippo(); // stampa hey
        pippo.saluta();
    }
}

class Over {
    public void saluta() {
        System.out.println("CIAO");
    }
}

class Pippo extends Over {
    // sovrascrivo il metodo saluta del padre over
    @Override
    public void saluta() {
        System.out.println("Hey, ho cambiato il metodo");
    }
}