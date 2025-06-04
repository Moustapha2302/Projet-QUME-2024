import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import org.example.*;

public class DistributeurAcceptanceTest {

    private Distributeur distributeur;
    private Utilisateur client;
    private Utilisateur personnel;
    private Stock stock;

    @BeforeEach
    void setUp() {
        distributeur = new Distributeur();
        distributeur.initialiserDistributeur();
        client = new Utilisateur("CLIENT001", "Moustapha Bah", false);
        personnel = new Utilisateur("STAFF001", "Lat Diop", true);
        stock = distributeur.obtenirStock();
    }

    @Test
    @DisplayName("Scénario 1 - Consultation des boissons disponibles par un client")
    void scenario01_ConsultationBoissonsDisponibles() {
        // CONTEXTE : Un client arrive devant le distributeur avec plusieurs boissons en stock

        // ACTION : Le client consulte la liste des boissons disponibles
        List<Boisson> boissonsDisponibles = distributeur.consulterBoissons();

        // RÉSULTAT ATTENDU : Affichage uniquement des boissons en stock avec nom, prix et quantité
        assertFalse(boissonsDisponibles.isEmpty(), "La liste des boissons ne doit pas être vide");

        for (Boisson boisson : boissonsDisponibles) {
            assertTrue(boisson.getQuantite() > 0,
                    "Toutes les boissons affichées doivent avoir une quantité > 0");
            assertNotNull(boisson.getNom(), "Chaque boisson doit avoir un nom");
            assertTrue(boisson.getPrix() > 0, "Chaque boisson doit avoir un prix > 0");
        }
    }

    @Test
    @DisplayName("Scénario 2 - Achat réussi d'une boisson avec montant exact")
    void scenario02_AchatMontantExact() {

        int stockInitial = stock.rechercherBoisson("Coca-Cola").getQuantite();

        // ACTION : Client insère exactement 500 FCFA et sélectionne Coca-Cola
        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", 500);

        // RÉSULTAT ATTENDU : Transaction réussie, pas de monnaie rendue, stock diminué
        assertTrue(transaction.estReussie(), "La transaction doit réussir");
        assertEquals(0, transaction.getMontantRendu(), "Aucune monnaie ne doit être rendue");
        assertEquals(stockInitial - 1, stock.rechercherBoisson("Coca-Cola").getQuantite(),
                "Le stock doit être diminué de 1");
    }

    @Test
    @DisplayName("Scénario 3 - Achat réussi avec rendu de monnaie ")
    void scenario03_AchatAvecRenduMonnaie() {
        int stockInitial = stock.rechercherBoisson("Pepsi").getQuantite();

        Transaction transaction = distributeur.acheterBoisson("Pepsi", 25500.0);

        assertTrue(transaction.estReussie(), "La transaction doit réussir");
        assertEquals(25000.0, transaction.getMontantRendu(), 0.01, "500 FCFA de monnaie doit être rendue");
        assertEquals(stockInitial - 1, stock.rechercherBoisson("Pepsi").getQuantite(),
                "Le stock doit être diminué de 1");
    }

    @Test
    @DisplayName("Scénario 4 - Tentative d'achat avec montant insuffisant")
    void scenario04_MontantInsuffisant() {

        int stockInitial = stock.rechercherBoisson("Coca-Cola").getQuantite();


        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", 499);

        // RÉSULTAT ATTENDU : Transaction échoue, stock inchangé
        assertFalse(transaction.estReussie(), "La transaction doit échouer");
        assertEquals(StatutTransaction.ECHEC_MONTANT_INSUFFISANT, transaction.getStatut());
        assertTrue(transaction.getMessageErreur().contains("Montant insuffisant"));
        assertEquals(stockInitial, stock.rechercherBoisson("Coca-Cola").getQuantite(),
                "Le stock ne doit pas changer");
    }

    @Test
    @DisplayName("Scénario 5 - Tentative d'achat d'une boisson en rupture de stock")
    void scenario05_RuptureStock() {
        // CONTEXTE : Boisson Fanta avec quantité 0
        stock.rechercherBoisson("Fanta").setQuantite(0);

        // ACTION : Client insère 1300 FCFA et tente de sélectionner Fanta
        Transaction transaction = distributeur.acheterBoisson("Fanta", 1300);

        // RÉSULTAT ATTENDU : Transaction échoue avec message rupture de stock
        assertFalse(transaction.estReussie(), "La transaction doit échouer");
        assertEquals(StatutTransaction.ECHEC_RUPTURE_STOCK, transaction.getStatut());
        assertTrue(transaction.getMessageErreur().contains("rupture de stock"));
    }

    @Test
    @DisplayName("Scénario 6 - Tentative d'achat d'une boisson inexistante")
    void scenario06_BoissonInexistante() {
        // CONTEXTE : Distributeur ne contient pas de "Red Bull"

        // ACTION : Client tente d'acheter Red Bull avec 1000 FCFA
        Transaction transaction = distributeur.acheterBoisson("Red Bull", 1000);

        // RÉSULTAT ATTENDU : Transaction échoue avec message boisson non trouvée
        assertFalse(transaction.estReussie(), "La transaction doit échouer");
        assertEquals(StatutTransaction.ECHEC_BOISSON_INEXISTANTE, transaction.getStatut());
        assertEquals("Boisson non trouvée", transaction.getMessageErreur());
    }

    @Test
    @DisplayName("Scénario 7 - Insertion de montant négatif")
    void scenario07_MontantNegatif() {
        // CONTEXTE : Client utilise l'interface du distributeur

        // ACTION : Client tente d'insérer -3280 FCFA (-5,00€)
        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", -3280);

        // RÉSULTAT ATTENDU : Système rejette avec message montant positif
        assertFalse(transaction.estReussie(), "La transaction doit échouer");
        assertEquals(StatutTransaction.ECHEC_MONTANT_INVALIDE, transaction.getStatut());
        assertEquals("Le montant doit être positif", transaction.getMessageErreur());
    }

    @Test
    @DisplayName("Scénario 8 - Rechargement de stock par personnel autorisé")
    void scenario08_RechargementPersonnelAutorise() {
        // CONTEXTE : Personnel autorisé accède au distributeur
        int stockInitial = stock.rechercherBoisson("Coca-Cola").getQuantite();

        // ACTION : Personnel recharge Coca-Cola avec 20 unités
        boolean resultat = personnel.rechargerStock(stock, "Coca-Cola", 20);

        // RÉSULTAT ATTENDU : Opération réussit, stock augmente de 20
        assertTrue(resultat, "Le rechargement doit réussir");
        assertEquals(stockInitial + 20, stock.rechercherBoisson("Coca-Cola").getQuantite(),
                "Le stock doit augmenter de 20 unités");
    }

    @Test
    @DisplayName("Scénario 9 - Tentative de rechargement par client non autorisé")
    void scenario09_RechargementClientNonAutorise() {
        // CONTEXTE : Client normal (non personnel)
        int stockInitial = stock.rechercherBoisson("Pepsi").getQuantite();

        // ACTION : Client tente de recharger Pepsi avec 10 unités
        boolean resultat = client.rechargerStock(stock, "Pepsi", 10);

        // RÉSULTAT ATTENDU : Opération échoue, stock inchangé
        assertFalse(resultat, "Le rechargement doit échouer");
        assertEquals(stockInitial, stock.rechercherBoisson("Pepsi").getQuantite(),
                "Le stock ne doit pas changer");
    }

    @Test
    @DisplayName("Scénario 10 - Ajout d'une nouvelle boisson par le personnel")
    void scenario10_AjoutNouvelleBoissonPersonnel() {
        // CONTEXTE : Personnel souhaite ajouter nouvelle boisson

        // ACTION : Personnel ajoute Ice Tea à 1181 FCFA (1,80€) avec stock de 15
        boolean resultat = distributeur.ajouterNouvelleBoisson("Ice Tea", 1181, 15);

        // RÉSULTAT ATTENDU : Nouvelle boisson disponible avec bon prix et stock
        assertTrue(resultat, "L'ajout doit réussir");
        Boisson nouvelleBuisson = stock.rechercherBoisson("Ice Tea");
        assertNotNull(nouvelleBuisson, "La nouvelle boisson doit exister");
        assertEquals(1181, nouvelleBuisson.getPrix(), "Le prix doit être correct");
        assertEquals(15, nouvelleBuisson.getQuantite(), "Le stock initial doit être correct");
    }

    @Test
    @DisplayName("Scénario 11 - Rendu de monnaie sans achat")
    void scenario11_RenduMonnaie() {
        // CONTEXTE : Client a inséré 3280 FCFA
        distributeur.insererMontant(client, 3280);
        assertEquals(3280, client.getPortefeuille().getMontant());

        // ACTION : Client demande le rendu de la monnaie
        int montantRendu = (int) distributeur.rendreMonnaie(client);

        // RÉSULTAT ATTENDU : Système rend 3280 FCFA, portefeuille à zéro
        assertEquals(3280, montantRendu, "Tout l'argent doit être rendu");
        assertEquals(0, client.getPortefeuille().getMontant(),
                "Le portefeuille doit être vide");
    }

    @Test
    @DisplayName("Scénario 12 - Consultation du journal des ventes par le personnel")
    void scenario12_ConsultationJournalVentes() {
        // CONTEXTE : Plusieurs transactions effectuées
        distributeur.acheterBoisson("Coca-Cola", 1312); // Réussie
        distributeur.acheterBoisson("Boisson Inexistante", 1312); // Échouée
        distributeur.acheterBoisson("Pepsi", 1312); // Réussie

        // ACTION : Personnel consulte l'historique
        List<Transaction> historique = distributeur.getJournal().getHistorique();

        // RÉSULTAT ATTENDU : Toutes les transactions affichées avec détails
        assertEquals(3, historique.size(), "Toutes les transactions doivent être enregistrées");

        for (Transaction transaction : historique) {
            assertNotNull(transaction.getId(), "Chaque transaction doit avoir un ID");
            assertNotNull(transaction.getStatut(), "Chaque transaction doit avoir un statut");
            assertNotNull(transaction.getDate(), "Chaque transaction doit avoir une date");
        }
    }

    @Test
    @DisplayName("Scénario 13 - Calcul du chiffre d'affaires ")
    void scenario13_CalculChiffreAffaires() {
        // CONTEXTE : 3 ventes réussies et 2 échouées
        distributeur.acheterBoisson("Coca-Cola", 1000);
        distributeur.acheterBoisson("Pepsi", 1000);
        distributeur.acheterBoisson("Fanta", 1000);
        distributeur.acheterBoisson("Boisson Inexistante", 1000);
        distributeur.acheterBoisson("Coca-Cola", 200);

        // ACTION : Personnel demande le chiffre d'affaires
        int chiffreAffaires = (int) distributeur.getJournal().getChiffreAffaires();


        assertEquals(1450, chiffreAffaires,
                "Le chiffre d'affaires doit être de 1450 FCFA (500 + 500 + 450)");
    }

    @Test
    @DisplayName("Scénario 14 - Statistiques de ventes par boisson")
    void scenario14_StatistiquesVentes() {
        // CONTEXTE : Plusieurs ventes réussies de différentes boissons
        distributeur.acheterBoisson("Coca-Cola", 1312);
        distributeur.acheterBoisson("Coca-Cola", 1312);
        distributeur.acheterBoisson("Coca-Cola", 1312);
        distributeur.acheterBoisson("Pepsi", 1312);
        distributeur.acheterBoisson("Pepsi", 1312);
        distributeur.acheterBoisson("Fanta", 1312);
        distributeur.acheterBoisson("Boisson Inexistante", 1312); // Échouée

        // ACTION : Personnel consulte les statistiques
        Map<String, Integer> stats = distributeur.getJournal().getStatistiquesVentes();

        // RÉSULTAT ATTENDU : Coca-Cola: 3, Pepsi: 2, Fanta: 1
        assertEquals(3, stats.get("Coca-Cola").intValue(), "Coca-Cola doit avoir 3 ventes");
        assertEquals(2, stats.get("Pepsi").intValue(), "Pepsi doit avoir 2 ventes");
        assertEquals(1, stats.get("Fanta").intValue(), "Fanta doit avoir 1 vente");
        assertNull(stats.get("Boisson Inexistante"),
                "Les échecs ne doivent pas compter dans les statistiques");
    }

    @Test
    @DisplayName("Scénario 15 - Gestion des transactions multiples simultanées")
    void scenario15_TransactionsMultiples() {
        // CONTEXTE : Mettre une boisson avec seulement 1 unité en stock
        Boisson boissonLimitee = stock.rechercherBoisson("Coca-Cola");
        boissonLimitee.setQuantite(1);

        // ACTION : Trois clients tentent d'acheter la même boisson
        Transaction transaction1 = distributeur.acheterBoisson("Coca-Cola", 1312);
        Transaction transaction2 = distributeur.acheterBoisson("Coca-Cola", 1312);
        Transaction transaction3 = distributeur.acheterBoisson("Coca-Cola", 1312);

        // RÉSULTAT ATTENDU : Seule la première réussit, les autres échouent
        assertTrue(transaction1.estReussie(), "La première transaction doit réussir");
        assertFalse(transaction2.estReussie(), "La deuxième transaction doit échouer");
        assertFalse(transaction3.estReussie(), "La troisième transaction doit échouer");
        assertEquals(0, boissonLimitee.getQuantite(), "Le stock final doit être 0");
    }

    @Test
    @DisplayName("Scénario 16 - Insertion de montants multiples")
    void scenario16_InsertionMontantsMultiples() {
        // CONTEXTE : Client veut acheter une boisson à 1500 FCFA
        distributeur.ajouterNouvelleBoisson("Boisson Chère", 1500, 5);

        // ACTION : Client insère 600 + 500 + 600 = 1700 FCFA
        distributeur.insererMontant(client, 600);
        distributeur.insererMontant(client, 500);
        distributeur.insererMontant(client, 600);

        // Vérification du cumul
        assertEquals(1700, client.getPortefeuille().getMontant(),
                "Le montant total doit être cumulé correctement");

        // Achat de la boisson
        Transaction transaction = distributeur.acheterBoisson("Boisson Chère",
                client.getPortefeuille().getMontant());

        // RÉSULTAT ATTENDU : Achat réussi avec 197 FCFA de monnaie
        assertTrue(transaction.estReussie(), "L'achat doit réussir");
        assertEquals(200, transaction.getMontantRendu(),
                "200 FCFA de monnaie doit être rendue");
    }

    @Test
    @DisplayName("Scénario 17 - Gestion d'une boisson avec quantité exacte")
    void scenario17_QuantiteExacte() {
        // CONTEXTE : Il reste exactement 1 unité d'une boisson
        Boisson boissonUnique = stock.rechercherBoisson("Coca-Cola");
        boissonUnique.setQuantite(1);

        // ACTION : Client achète cette dernière unité avec montant exact
        Transaction transaction = distributeur.acheterBoisson("Coca-Cola", 985);

        // RÉSULTAT ATTENDU : Achat réussi, stock à 0, boisson plus disponible
        assertTrue(transaction.estReussie(), "L'achat doit réussir");
        assertEquals(0, boissonUnique.getQuantite(), "Le stock doit passer à 0");
        assertFalse(stock.estBoissonDisponible("Coca-Cola"),
                "La boisson ne doit plus être disponible");

        // Vérification que la boisson n'apparaît plus dans la liste
        List<Boisson> boissonsDisponibles = distributeur.consulterBoissons();
        assertTrue(boissonsDisponibles.stream()
                        .noneMatch(b -> b.getNom().equals("Coca-Cola")),
                "Coca-Cola ne doit plus apparaître dans les boissons disponibles");
    }

    @Test
    @DisplayName("Scénario 18 - Rechargement avec quantité invalide")
    void scenario18_RechargementQuantiteInvalide() {
        // CONTEXTE : Rechargement du stock
        int stockInitial = stock.rechercherBoisson("Coca-Cola").getQuantite();

        // ACTION : Essayer d'ajouter -5 unités
        assertThrows(IllegalArgumentException.class, () -> {
            distributeur.rechargerStock("Coca-Cola", -5);
        }, "Une exception doit être levée pour quantité négative");

        // ACTION : Essayer d'ajouter 0 unité
        assertThrows(IllegalArgumentException.class, () -> {
            distributeur.rechargerStock("Coca-Cola", 0);
        }, "Une exception doit être levée pour quantité nulle");

        // RÉSULTAT ATTENDU : Stock inchangé
        assertEquals(stockInitial, stock.rechercherBoisson("Coca-Cola").getQuantite(),
                "Le stock ne doit pas changer avec des quantités invalides");
    }
}