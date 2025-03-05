
class Adulto {
    String nome;

    public Adulto(String nome) {
        this.nome = nome;
    }

    public void stampaAdulto(){
        System.out.println("adulto: " + this.nome);
    }

    public class Figlio {
        String nomeFiglio;
        
        public Figlio(String nomeFiglio) {
            this.nomeFiglio = nomeFiglio;
        }

        public void stampaFiglio(){
            System.out.println("figlio: " + this.nomeFiglio);
        }
    }

}

public class EsercizioInnerClass {
    public static void main(String[] args) {
        Adulto adulto1 = new Adulto("Topolino");
        Adulto.Figlio figlio1 = adulto1.new Figlio("Pippo");
        
        System.out.println("Famiglia di " + adulto1.nome + ", Figlio:" + figlio1.nomeFiglio);

        adulto1.stampaAdulto();
        figlio1.stampaFiglio();
    }
}
