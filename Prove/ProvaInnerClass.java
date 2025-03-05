class Classefuori {
    int x = 10;

    class Classedentro {
        int x = 5;
    }
}

public class ProvaInnerClass {
    public static void main(String[] args) {
        Classefuori Fuori = new Classefuori();
        Classefuori.Classedentro Dentro = Fuori.new Classedentro();
        System.out.println(Dentro.x + " /// " + Fuori.x);
    }
}