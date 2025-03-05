
class Adulto {
    String nome;

    public Adulto(String nome) {
        this.nome = nome;
    }

    public class Figlio {
        String nomeFiglio;
        
        public Figlio(String nomeFiglio) {
            this.nomeFiglio = nomeFiglio;
        }
    }

}

public class EsercizioInnerClass {
    public static void main(String[] args) {
        // Creazione degli oggetti Adulto
        Adulto genitore1 = new Adulto("Topolino");
        Adulto.Figlio figlio1 = genitore1.new Figlio("Pippo");
        // Creazione dei figli per ogni genitore
        System.out.println("Famiglia di " + genitore1.nome + ", Figlio:" + figlio1.nomeFiglio);
    }
}
