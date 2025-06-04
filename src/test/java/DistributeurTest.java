import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.time.LocalDateTime;
import org.example.Distributeur;
import org.example.Utilisateur;
import org.example.Stock;
import org.example.Boisson;
import org.example.Portefeuille;
import org.example.Transaction;
import org.example.StatutTransaction;
import org.example.JournalVentes;

public class DistributeurTest {

    private Distributeur distributeur;
    private Utilisateur client;
    private Utilisateur personnel;
    private Stock stock;

    @BeforeEach
    void setUp() {
        distributeur = new Distributeur();
        distributeur.initialiserDistributeur();
        client = new Utilisateur("U001", "Moustapha Bah", false);
        personnel = new Utilisateur("U002", "Sidy Mamadou Dia", true);
        stock = distributeur.obtenirStock();
    }

    // =TESTS CLASSE BOISSON =//

    @Test
    @DisplayName("Test 01 - Création d'une boisson avec valeurs valides")
    void testCreationBoissonValide() {
        Boisson boisson = new Boisson("Test Cola", 500.00, 10);

        assertEquals("Test Cola", boisson.getNom());
        assertEquals(500.00, boisson.getPrix(), 0.01);
        assertEquals(10, boisson.getQuantite());
    }

    @Test
    @DisplayName("Test 02 - Modification des attributs d'une boisson")
    void testModificationBoisson() {
        Boisson boisson = new Boisson("Cola", 500.00, 10);

        boisson.setNom("Présséa");
        boisson.setPrix(700.00);
        boisson.setQuantite(15);

        assertEquals("Présséa", boisson.getNom());
        assertEquals(700.00, boisson.getPrix(), 0.01);
        assertEquals(15, boisson.getQuantite());
    }

    @Test
    @DisplayName("Test 03 - Égalité entre boissons basée sur le nom")
    void testEgaliteBoissons() {
        Boisson boisson1 = new Boisson("Cola", 500, 10);
        Boisson boisson2 = new Boisson("Cola", 500, 5);
        Boisson boisson3 = new Boisson("Pepsi", 500, 10);

        assertEquals(boisson1, boisson2); // Même nom
        assertNotEquals(boisson1, boisson3); // Noms différents
    }

    // ==== TESTS CLASSE STOCK =//

    @Test
    @DisplayName("Test 04 - Ajout d'une nouvelle boisson au stock")
    void testAjoutNouvelleBoisson() {
        Stock stock = new Stock(1, 50);
        Boisson boisson = new Boisson("Nouvelle Boisson", 450, 8);

        stock.ajouterBoisson(boisson);

        assertNotNull(stock.rechercherBoisson("Nouvelle Boisson"));
        assertEquals(8, stock.rechercherBoisson("Nouvelle Boisson").getQuantite());
    }

    @Test
    @DisplayName("Test 05 - Ajout de quantité à une boisson existante")
    void testAjoutQuantiteBoisson() {
        Stock stock = new Stock(1, 50);
        stock.ajouterBoisson(new Boisson("Cola", 500, 5));
        stock.ajouterBoisson(new Boisson("Cola", 500, 3)); // Même nom

        assertEquals(8, stock.rechercherBoisson("Cola").getQuantite());
    }

    @Test
    @DisplayName("Test 06 - Recherche de boisson existante")
    void testRechercheBoissonExistante() {
        Boisson boisson = stock.rechercherBoisson("Coca-Cola");

        assertNotNull(boisson);
        assertEquals("Coca-Cola", boisson.getNom());
    }

    @Test
    @DisplayName("Test 07 - Recherche de boisson inexistante")
    void testRechercheBoissonInexistante() {
        Boisson boisson = stock.rechercherBoisson("Annas");

        assertNull(boisson);
    }

    @Test
    @DisplayName("Test 08 - Vérification disponibilité boisson en stock")
    void testBoissonDisponible() {
        assertTrue(stock.estBoissonDisponible("Coca-Cola"));
        assertFalse(stock.estBoissonDisponible("Vimto"));
    }

    @Test
    @DisplayName("Test 09 - Réduction de quantité de boisson")
    void testReductionQuantite() {
        int quantiteInitiale = stock.rechercherBoisson("Coca-Cola").getQuantite();
        stock.reduireQuantite("Coca-Cola", 2);

        assertEquals(quantiteInitiale - 2, stock.rechercherBoisson("Coca-Cola").getQuantite());
    }

    @Test
    @DisplayName("Test 10 - Détection rupture de stock")
    void testRuptureStock() {
        // Réduire le stock à 0
        Boisson boisson = stock.rechercherBoisson("Coca-Cola");
        boisson.setQuantite(0);

        assertFalse(stock.estBoissonDisponible("Coca-Cola"));
    }

    //==== TESTS CLASSE PORTEFEUILLE ======//

    @Test
    @DisplayName("Test 11 - Insertion de montant valide dans portefeuille")
    void testInsertionMontantValide() {
        Portefeuille portefeuille = new Portefeuille();

        portefeuille.insererMontant(600.00);

        assertEquals(600.00, portefeuille.getMontant(), 0.01);
    }

    @Test
    @DisplayName("Test 12 - Insertion de montant négatif (erreur)")
    void testInsertionMontantNegatif() {
        Portefeuille portefeuille = new Portefeuille();

        assertThrows(IllegalArgumentException.class, () -> {
            portefeuille.insererMontant(-250.00);
        });
    }

    @Test
    @DisplayName("Test 13 - Vérification capacité d'achat")
    void testCapaciteAchat() {
        Portefeuille portefeuille = new Portefeuille();
        portefeuille.insererMontant(600.00);

        assertTrue(portefeuille.peutAcheter(500.00));
        assertTrue(portefeuille.peutAcheter(450.00));
        assertFalse(portefeuille.peutAcheter(650.00));
    }

    @Test
    @DisplayName("Test 14 - Retour de montant du portefeuille")
    void testRetourMontant() {
        Portefeuille portefeuille = new Portefeuille();
        portefeuille.insererMontant(600.00);

        double montantRetourne = portefeuille.retournerMontant(300.00);

        assertEquals(300.00, montantRetourne, 0.01);
        assertEquals(300.00, portefeuille.getMontant(), 0.01);
    }

    // ======== TESTS CLASSE UTILISATEUR =========//

    @Test
    @DisplayName("Test 15 - Création utilisateur client")
    void testCreationUtilisateurClient() {
        Utilisateur user = new Utilisateur("U001", "Moustapha Bah", false);

        assertEquals("U001", user.getId());
        assertEquals("Moustapha Bah", user.getNom());
        assertFalse(user.estPersonnel());
        assertNotNull(user.getPortefeuille());
    }

    @Test
    @DisplayName("Test 16 - Création utilisateur personnel")
    void testCreationUtilisateurPersonnel() {
        Utilisateur user = new Utilisateur("U002", "Sidy Mamadou Dia", true);

        assertTrue(user.estPersonnel());
    }

    @Test
    @DisplayName("Test 17 - Rechargement stock par personnel autorisé")
    void testRechargementStockPersonnel() {
        int quantiteInitiale = stock.rechercherBoisson("Coca-Cola").getQuantite();

        boolean resultat = personnel.rechargerStock(stock, "Coca-Cola", 5);

        assertTrue(resultat);
        assertEquals(quantiteInitiale + 5, stock.rechercherBoisson("Coca-Cola").getQuantite());
    }

    @Test
    @DisplayName("Test 18 - Rechargement stock par client non autorisé")
    void testRechargementStockClientNonAutorise() {
        boolean resultat = client.rechargerStock(stock, "Coca-Cola", 5);

        assertFalse(resultat);
    }

    // ======== TESTS CLASSE TRANSACTION =========//

    @Test
    @DisplayName("Test 19 - Création transaction réussie")
    void testCreationTransactionReussie() {
        Boisson boisson = new Boisson("Cola", 500.00, 10);
        Transaction transaction = new Transaction("TXN001", boisson, 500.00);

        assertEquals("TXN001", transaction.getId());
        assertEquals(boisson, transaction.getBoisson());
        assertEquals(500.00, transaction.getMontant(), 0.01);
        assertEquals(StatutTransaction.REUSSIE, transaction.getStatut());
        assertNotNull(transaction.getDate());
    }

    @Test
    @DisplayName("Test 20 - Calcul montant rendu transaction")
    void testCalculMontantRenduTransaction() {
        Boisson boisson = new Boisson("Cola", 500.00, 10);
        Transaction transaction = new Transaction("TXN001", boisson, 10000.00);

        double montantRendu = transaction.calculerMontantRendu();

        assertEquals(9500.00, montantRendu, 0.01);
    }

    @Test
    @DisplayName("Test 21 - Transaction avec statut d'échec")
    void testTransactionEchec() {
        Transaction transaction = new Transaction("TXN002", null, 500.00);
        transaction.setStatut(StatutTransaction.ECHEC_BOISSON_INEXISTANTE);
        transaction.setMessageErreur("Boisson non trouvée");

        assertFalse(transaction.estReussie());
        assertEquals("Boisson non trouvée", transaction.getMessageErreur());
    }

    // ======= TESTS CLASSE DISTRIBUTEUR ======//

    @Test
    @DisplayName("Test 22 - Consultation des boissons disponibles")
    void testConsultationBoissonsDisponibles() {
        List<Boisson> boissons = distributeur.consulterBoissons();

        assertFalse(boissons.isEmpty());
        assertTrue(boissons.stream().allMatch(b -> b.getQuantite() > 0));
    }

    @Test
    @DisplayName("Test 23 - Achat boisson avec succès")
    void testAchatBoissonSucces() {
        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", 2000.00);

        assertEquals(StatutTransaction.REUSSIE, transaction.getStatut());
        assertTrue(transaction.estReussie());
        assertEquals(1500.00, transaction.getMontantRendu(), 0.01);
    }

    @Test
    @DisplayName("Test 24 - Achat boisson avec montant insuffisant")
    void testAchatMontantInsuffisant() {
        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", 495.00);

        assertEquals(StatutTransaction.ECHEC_MONTANT_INSUFFISANT, transaction.getStatut());
        assertFalse(transaction.estReussie());
        assertTrue(transaction.getMessageErreur().contains("Montant insuffisant"));
    }

    @Test
    @DisplayName("Test 25 - Achat boisson inexistante")
    void testAchatBoissonInexistante() {
        Transaction transaction = distributeur.acheterBoisson("Boisson Inexistante", 450.00);

        assertEquals(StatutTransaction.ECHEC_BOISSON_INEXISTANTE, transaction.getStatut());
        assertEquals("Boisson non trouvée", transaction.getMessageErreur());
    }

    @Test
    @DisplayName("Test 26 - Achat boisson en rupture de stock")
    void testAchatBoissonRuptureStock() {
        // Mettre une boisson en rupture de stock
        Boisson boisson = stock.rechercherBoisson("Coca-Cola");
        boisson.setQuantite(0);

        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", 500.00);

        assertEquals(StatutTransaction.ECHEC_RUPTURE_STOCK, transaction.getStatut());
        assertTrue(transaction.getMessageErreur().contains("rupture de stock"));
    }

    @Test
    @DisplayName("Test 27 - Achat avec montant négatif")
    void testAchatMontantNegatif() {
        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", -200.00);

        assertEquals(StatutTransaction.ECHEC_MONTANT_INVALIDE, transaction.getStatut());
        assertEquals("Le montant doit être positif", transaction.getMessageErreur());
    }

    @Test
    @DisplayName("Test 28 - Achat avec montant zéro")
    void testAchatMontantZero() {
        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", 0.00);

        assertEquals(StatutTransaction.ECHEC_MONTANT_INVALIDE, transaction.getStatut());
    }

    @Test
    @DisplayName("Test 29 - Rechargement stock distributeur")
    void testRechargementStockDistributeur() {
        int quantiteInitiale = stock.rechercherBoisson("Coca-Cola").getQuantite();

        boolean resultat = distributeur.rechargerStock("Coca-Cola", 10);

        assertTrue(resultat);
        assertEquals(quantiteInitiale + 10, stock.rechercherBoisson("Coca-Cola").getQuantite());
    }

    @Test
    @DisplayName("Test 30 - Ajout nouvelle boisson au distributeur")
    void testAjoutNouvelleBoissonDistributeur() {
        boolean resultat = distributeur.ajouterNouvelleBoisson("King Pomme", 200.00, 12);

        assertTrue(resultat);
        assertNotNull(stock.rechercherBoisson("King Pomme"));
        assertEquals(200.00, stock.rechercherBoisson("King Pomme").getPrix(), 0.01);
    }

    @Test
    @DisplayName("Test 31 - Insertion montant utilisateur")
    void testInsertionMontantUtilisateur() {
        double montantTotal = distributeur.insererMontant(client, 2500.00);

        assertEquals(2500.00, montantTotal, 0.01);
        assertEquals(2500.00, client.getPortefeuille().getMontant(), 0.01);
    }

    @Test
    @DisplayName("Test 32 - Rendu de monnaie utilisateur")
    void testRenduMonnaire() {
        client.getPortefeuille().insererMontant(3500.00);

        double montantRendu = distributeur.rendreMonnaie(client);

        assertEquals(3500.00, montantRendu, 0.01);
        assertEquals(0.00, client.getPortefeuille().getMontant(), 0.01);
    }

    @Test
    @DisplayName("Test 33 - Calcul montant rendu distributeur")
    void testCalculMontantRenduDistributeur() {
        double montantRendu = distributeur.calculerMontantRendu("Coca-Cola", 2000.00);

        assertEquals(1500.00, montantRendu, 0.01);
    }

    //======== TESTS CLASSE JOURNAL VENTES =======//

    @Test
    @DisplayName("Test 34 - Ajout transaction au journal")
    void testAjoutTransactionJournal() {
        JournalVentes journal = new JournalVentes();
        Boisson boisson = new Boisson("Cola", 500.00, 10);
        Transaction transaction = new Transaction("TXN001", boisson, 600.00);

        journal.ajouterTransaction(transaction);

        assertEquals(1, journal.getHistorique().size());
        assertTrue(journal.getHistorique().contains(transaction));
    }

    @Test
    @DisplayName("Test 35 - Calcul chiffre d'affaires")
    void testCalculChiffreAffaires() {
        JournalVentes journal = new JournalVentes();

        // Transactions réussies
        Boisson boisson1 = new Boisson("Cola", 500.00, 10);
        Transaction transaction1 = new Transaction("TXN001", boisson1, 600.00);
        journal.ajouterTransaction(transaction1);

        Boisson boisson2 = new Boisson("Pepsi", 500.00, 10);
        Transaction transaction2 = new Transaction("TXN002", boisson2, 600.00);
        journal.ajouterTransaction(transaction2);

        // Transaction échouée (ne doit pas compter)
        Transaction transaction3 = new Transaction("TXN003", null, 300.00);
        transaction3.setStatut(StatutTransaction.ECHEC_BOISSON_INEXISTANTE);
        journal.ajouterTransaction(transaction3);

        double chiffreAffaires = journal.getChiffreAffaires();

        assertEquals(1000.00, chiffreAffaires, 0.01);
    }

    @Test
    @DisplayName("Test 36 - Statistiques de ventes par boisson")
    void testStatistiquesVentes() {
        // Effectuer plusieurs achats avec des montants suffisants
        distributeur.acheterBoisson("Coca-Cola", 500.00);
        distributeur.acheterBoisson("Coca-Cola", 600.00);
        distributeur.acheterBoisson("Pepsi", 500.00);

        var stats = distributeur.getJournal().getStatistiquesVentes();

        assertEquals(2, stats.get("Coca-Cola").intValue());
        assertEquals(1, stats.get("Pepsi").intValue());
    }
}
