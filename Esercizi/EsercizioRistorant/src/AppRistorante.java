import java.sql.*;
import java.util.Random;
import java.util.Scanner;

// Classe per la connessione al database
class DBContext {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ristorantedb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public static Connection connessioneDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Classe Utente
class Utente {
    private int id;
    private String nome;
    private String email;
    private float soldi;
    private String ruolo;

    public Utente(int id, String nome, String email, float soldi, String ruolo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.soldi = soldi;
        this.ruolo = ruolo;
    }

    public int getId() {
        return id;
    }

    public String getRuolo() {
        return ruolo;
    }

    public float getSoldi() {
        return soldi;
    }

    public void stampaDati(Utente utente) {
        System.out.println("\n--- Dati Utente ---");
        System.out.println("Nome: " + nome);
        System.out.println("Email: " + email);
        System.out.println("Ruolo: " + ruolo);
        if (utente.getRuolo().equals("Critico") || utente.getRuolo().equals("Critico_Forte"))
            System.out.println("Credito: " + String.format("%.2f", soldi));
    }
}

// Classe Ristorante
class Ristorante {
    protected Connection conn;

    public Ristorante() {
        this.conn = DBContext.connessioneDatabase();
    }

    public void stampaMenu() {
        System.out.println("\n--- Menu del Ristorante ---");
        try {
            String query = "SELECT p.id, p.nome_piatto, p.prezzo, u.nome AS chef_nome " +
                    "FROM piatti p " +
                    "JOIN utenti u ON p.chef_id = u.id";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.isBeforeFirst()) {
                System.out.println("Nessun piatto disponibile.");
            } else {
                while (rs.next()) {
                    System.out.println(
                            "Piatto: " + rs.getString("nome_piatto") +
                                    "| Prezzo: " + rs.getFloat("prezzo") +
                                    "| Chef: " + rs.getString("chef_nome"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stampaValutazioni() {
        System.out.println("\n--- Valutazioni ---");
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt
                    .executeQuery("SELECT utenti.nome, piatti.nome_piatto, valutazioni.recensione FROM valutazioni " +
                            "JOIN utenti ON valutazioni.critico_id = utenti.id " +
                            "JOIN piatti ON valutazioni.piatto_id = piatti.id");

            if (!rs.isBeforeFirst()) {
                System.out.println("Nessuna recensione disponibile.");
            } else {
                while (rs.next()) {
                    System.out.println(rs.getString("nome") + " ha recensito " +
                            rs.getString("nome_piatto") + ": " + rs.getString("recensione"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Classe Chef
class Chef extends Ristorante {
    private int chefId;

    public Chef(int chefId) {
        this.chefId = chefId;
    }

    public void aggiungiPiatto(String piatto, float prezzo) {
        try {
            PreparedStatement ps = conn
                    .prepareStatement("INSERT INTO piatti (nome_piatto, chef_id, prezzo) VALUES (?, ?, ?)");
            ps.setString(1, piatto);
            ps.setInt(2, chefId);
            ps.setFloat(3, prezzo);
            ps.executeUpdate();
            System.out.println("Piatto aggiunto: " + piatto + " | Prezzo: euro " + String.format("%.2f", prezzo));

            // Verifica se lo chef ha raggiunto 3 piatti e aggiorna il ruolo
            PreparedStatement countPs = conn
                    .prepareStatement("SELECT COUNT(*) AS numPiatti FROM piatti WHERE chef_id = ?");
            countPs.setInt(1, chefId);
            ResultSet countRs = countPs.executeQuery();
            if (countRs.next() && countRs.getInt("numPiatti") == 3) {
                // Aggiorna il ruolo a Chef_Capo
                PreparedStatement updateRolePs = conn
                        .prepareStatement("UPDATE utenti SET ruolo = 'Chef_Capo' WHERE id = ?");
                updateRolePs.setInt(1, chefId);
                updateRolePs.executeUpdate();
                System.out.println("Congratulazioni! Sei diventato un Chef_Capo.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Classe Critico
class Critico extends Ristorante {
    private int criticoId;

    public Critico(int criticoId) {
        this.criticoId = criticoId;
    }

    public void aggiungiValutazione(String nomePiatto, String recensione) {
        try {
            // Trova l'ID del piatto in base al nome
            PreparedStatement ps1 = conn.prepareStatement("SELECT id FROM piatti WHERE nome_piatto = ?");
            ps1.setString(1, nomePiatto);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int piattoId = rs.getInt("id");

                // Inserisci la valutazione
                PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO valutazioni (critico_id, piatto_id, recensione) VALUES (?, ?, ?)");
                ps2.setInt(1, criticoId);
                ps2.setInt(2, piattoId);
                ps2.setString(3, recensione);
                ps2.executeUpdate();

                System.out.println("Recensione aggiunta con successo per il piatto: " + nomePiatto);

                // Verifica se il critico ha scritto 3 recensioni e aggiorna il ruolo
                PreparedStatement countPs = conn
                        .prepareStatement("SELECT COUNT(*) AS numRecensioni FROM valutazioni WHERE critico_id = ?");
                countPs.setInt(1, criticoId);
                ResultSet countRs = countPs.executeQuery();
                if (countRs.next() && countRs.getInt("numRecensioni") == 3) {
                    // Aggiorna il ruolo a Critico_Forte
                    PreparedStatement updateRolePs = conn
                            .prepareStatement("UPDATE utenti SET ruolo = 'Critico_Forte' WHERE id = ?");
                    updateRolePs.setInt(1, criticoId);
                    updateRolePs.executeUpdate();
                    System.out.println("Congratulazioni! Sei diventato un Critico_Forte.");
                }
            } else {
                System.out.println("Errore: Il piatto '" + nomePiatto + "' non esiste nel database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean ordinaPiatto(String nomePiatto, Utente critico) {
        try {
            // Recupera il prezzo del piatto
            PreparedStatement ps = conn.prepareStatement("SELECT prezzo FROM piatti WHERE nome_piatto = ?");
            ps.setString(1, nomePiatto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                float prezzo = rs.getFloat("prezzo");

                // Controlla se il critico ha abbastanza soldi
                if (critico.getSoldi() < prezzo) {
                    System.out.println("Saldo insufficiente per ordinare questo piatto.");
                    return false;
                }

                // Scala i soldi dal critico
                PreparedStatement updatePs = conn
                        .prepareStatement("UPDATE utenti SET soldi = soldi - ? WHERE id = ?");
                updatePs.setFloat(1, prezzo);
                updatePs.setInt(2, critico.getId());
                updatePs.executeUpdate();

                System.out.println("Hai ordinato il piatto " + nomePiatto + " per euro " + prezzo);
                System.out.println("Soldi attuali: " + String.format("%.2f", critico.getSoldi()));
                return true;
            } else {
                System.out.println("Piatto non trovato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

// Classe Menu
class Menu {
    private Scanner scanner;
    private Connection conn;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.conn = DBContext.connessioneDatabase();
    }

    public void menuPrincipale() {
        Utente utente = null;

        while (utente == null) {
            System.out.println("\n--- Benvenuto nel Ristorante! ---");
            System.out.println("1. Registrazione");
            System.out.println("2. Login");
            System.out.println("3. Stampa recensioni");
            System.out.println("5. Stampa menu");
            System.out.print("Scegli un'opzione: ");
            int scelta = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();
            Ristorante ristorante = new Ristorante();

            if (scelta == 1) {
                utente = registraUtente();
            } else if (scelta == 2) {
                utente = loginUtente();
            } else if (scelta == 3) {
                ristorante.stampaValutazioni();
            } else if (scelta == 4) {
                ristorante.stampaMenu();
            } else {
                System.out.println("Scelta non valida, riprova");
            }
        }
        utente.stampaDati(utente);
        checkRoleMenu(utente);
    }

    private Utente registraUtente() {
        try {
            System.out.print("Nome: ");
            String nome = Controlli.controlloInputStringhe(scanner);

            String email = "";
            boolean emailValida = false;

            // Chiediamo un'email finché non è valida (non esistente nel DB)
            while (!emailValida) {
                System.out.print("Email: ");
                email = Controlli.controlloInputStringhe(scanner);

                // Verifica che l'email non sia già presente nel database
                PreparedStatement psCheckEmail = conn.prepareStatement("SELECT COUNT(*) FROM utenti WHERE email = ?");
                psCheckEmail.setString(1, email);
                ResultSet rsCheckEmail = psCheckEmail.executeQuery();

                if (rsCheckEmail.next() && rsCheckEmail.getInt(1) > 0) {
                    System.out.println("L'email è già presente. Per favore, inserisci una nuova email.");
                } else {
                    emailValida = true; // Se l'email non esiste, è valida
                }
            }

            System.out.print("Password: ");
            String password = Controlli.controlloInputStringhe(scanner);
            float soldi = new Random().nextFloat() * (100 - 10) + 10;

            System.out.println("Scegli il tuo ruolo:");
            System.out.println("1. Chef");
            System.out.println("2. Critico");
            int ruoloScelto = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();
            String ruolo = (ruoloScelto == 1) ? "Chef" : "Critico";

            // Inseriamo l'utente nel database
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO utenti (nome, email, password, soldi, ruolo) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setFloat(4, soldi);
            ps.setString(5, ruolo);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return new Utente(rs.getInt(1), nome, email, soldi, ruolo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Utente loginUtente() {
        try {
            System.out.print("Email: ");
            String email = Controlli.controlloInputStringhe(scanner);
            System.out.print("Password: ");
            String password = Controlli.controlloInputStringhe(scanner);

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM utenti WHERE email = ? AND password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Utente(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("email"), rs.getFloat("soldi"), rs.getString("ruolo"));
            } else {
                System.out.println("Credenziali errate. Riprova.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkRoleMenu(Utente utente) {
        if (utente.getRuolo().equals("Chef") || utente.getRuolo().equals("Chef_Capo")) {
            menuChef(utente);
        } else if (utente.getRuolo().equals("Critico") || utente.getRuolo().equals("Critico_Forte")) {
            menuCritico(utente);
        } else {
            System.out
                    .println("Utente Normale, non puoi fare niente non hai ancora un ruolo, contattare l admin or idk");
            utente = null;
            menuPrincipale();
        }
    }

    private void menuChef(Utente chef) {
        Chef chefObj = new Chef(chef.getId());

        while (true) {
            System.out.println("\n--- Menu Chef ---");
            System.out.println("1. Aggiungere un Piatto");
            System.out.println("2. Stampa Menu");
            System.out.println("3. Esci");
            System.out.print("Scelta: ");
            int scelta = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            if (scelta == 1) {
                System.out.print("Nome del piatto: ");
                String piatto = Controlli.controlloInputStringhe(scanner);
                System.out.print("Prezzo del piatto: ");
                Float prezzo = scanner.nextFloat();
                chefObj.aggiungiPiatto(piatto, prezzo);
            } else if (scelta == 2) {
                chefObj.stampaMenu();
            } else if (scelta == 3) {
                chef = null;
                break;
            } else {
                System.out.println("Scelta non valida, riprova");
            }

        }
    }

    private void menuCritico(Utente critico) {
        Critico criticoObj = new Critico(critico.getId());

        while (true) {
            System.out.println("\n--- Menu Critico ---");
            System.out.println("1. Ordinare un Piatto");
            System.out.println("2. Aggiungere una Recensione");
            System.out.println("3. Stampa Valutazioni");
            System.out.println("4. Esci");
            System.out.print("Scelta: ");
            int scelta = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            if (scelta == 1) {
                criticoObj.stampaMenu();
                System.out.print("Nome del piatto da ordinare: ");
                String nomePiatto = Controlli.controlloInputStringhe(scanner);
                criticoObj.ordinaPiatto(nomePiatto, critico);
            } else if (scelta == 2) {
                criticoObj.stampaMenu();
                System.out.print("nome del piatto da recensire: ");
                String nomePiatto = Controlli.controlloInputStringhe(scanner);
                System.out.print("Inserisci la recensione: ");
                String recensione = Controlli.controlloInputStringhe(scanner);
                criticoObj.aggiungiValutazione(nomePiatto, recensione);
            } else if (scelta == 3) {
                criticoObj.stampaValutazioni();
            } else if (scelta == 4) {
                critico = null;
                break;
            } else {
                System.out.println("Scelta non valida, riprova");
            }
        }
    }
}

// Classe Controlli
class Controlli {
    // Metodo per controllare che l'input stringa non sia vuoto
    public static String controlloInputStringhe(Scanner scanner) {
        String valore;
        do {
            valore = scanner.nextLine().trim();
            if (valore.isEmpty()) {
                System.out.print("Input non valido. Inserisci un testo: ");
            }
        } while (valore.isEmpty());
        return valore;
    }

    // Metodo per controllare l'input intero
    public static int controlloInputInteri(Scanner scanner) {
        int valore;
        while (true) {
            if (!scanner.hasNextInt()) {
                System.out.print("Devi inserire un numero intero. Riprova: ");
                scanner.next(); // Consuma l'input errato
            } else {
                valore = scanner.nextInt();
                if (valore >= 0) {
                    return valore;
                }
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        }
    }
}

// Main
public class AppRistorante {
    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.menuPrincipale();
    }
}