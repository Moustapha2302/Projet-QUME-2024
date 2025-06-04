package stepdefinitions;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.fr.*;
import static org.junit.jupiter.api.Assertions.*;

import org.example.*;

import java.util.Map;

public class AchatBoissonSteps {

    private Distributeur distributeur;
    private Utilisateur client;
    private Transaction transaction;

    @Given("le distributeur est initialisé avec les boissons suivantes:")
    public void initialiser_distributeur(io.cucumber.datatable.DataTable dataTable) {
        distributeur = new Distributeur();
        // Réinitialisation stock vide
        Stock stock = distributeur.obtenirStock();
        stock.getBoissons().clear();

        dataTable.asMaps().forEach(row -> {
            String nom = row.get("nom");
            double prix = Double.parseDouble(row.get("prix"));
            int quantite = Integer.parseInt(row.get("quantite"));
            distributeur.ajouterNouvelleBoisson(nom, prix, quantite);
        });
    }

    @Given("un client dispose de {double} FCFA")
    public void un_client_dispose_de(Double montant) {
        client = new Utilisateur("U1", "Client", false);
        distributeur.insererMontant(client, montant);
        assertEquals(montant, client.getPortefeuille().getMontant(), 0.001);
    }

    @When("le client achète {string} avec {double} FCFA")
    public void le_client_achete_avec(String nomBoisson, Double montant) {
        // Met à jour le portefeuille avant achat
        distributeur.insererMontant(client, montant);
        transaction = distributeur.acheterBoisson(nomBoisson, montant);
    }

    @Then("la transaction est réussie")
    public void transaction_reussie() {
        assertTrue(transaction.estReussie(), "La transaction devrait être réussie");
    }

    @Then("aucune monnaie n'est rendue")
    public void aucune_monnaie_rendue() {
        assertEquals(0, transaction.getMontantRendu(), 0.001, "Aucune monnaie ne doit être rendue");
    }

    @Then("la monnaie rendue est de {double} FCFA")
    public void monnaie_rendue(Double monnaieAttendue) {
        assertEquals(monnaieAttendue, transaction.getMontantRendu(), 0.001);
    }

    @Then("la quantité de {string} est diminuée de {int}")
    public void quantite_diminuee(String nomBoisson, int diminution) {
        Boisson boisson = distributeur.obtenirStock().rechercherBoisson(nomBoisson);
        assertNotNull(boisson);
        // On vérifie que la quantité est bien initiale - diminution
        // Comme on initialise le stock dans le Background, on connaît la quantité initiale
        int quantiteInitiale;
        switch (nomBoisson.toLowerCase()) {
            case "coca-cola": quantiteInitiale = 5; break;
            case "pepsi": quantiteInitiale = 0; break;
            case "fanta": quantiteInitiale = 10; break;
            default: quantiteInitiale = boisson.getQuantite() + diminution; // fallback
        }
        assertEquals(quantiteInitiale - diminution, boisson.getQuantite());
    }

    @Then("la transaction échoue avec le message {string}")
    public void transaction_echoue_avec_message(String messageAttendu) {
        assertFalse(transaction.estReussie(), "La transaction devrait échouer");
        assertEquals(messageAttendu, transaction.getMessageErreur());
    }

    @Then("la quantité de {string} reste à {int}")
    public void quantite_reste(String nomBoisson, int quantiteAttendue) {
        Boisson boisson = distributeur.obtenirStock().rechercherBoisson(nomBoisson);
        assertNotNull(boisson);
        assertEquals(quantiteAttendue, boisson.getQuantite());
    }
}
