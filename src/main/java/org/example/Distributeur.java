package org.example;

import java.util.*;

public class Distributeur {
    private Stock stock;
    private JournalVentes journal;
    private int compteurTransaction;

    public Distributeur() {
        this.stock = new Stock(2, 50); // Quantité minimale: 2, maximale: 50
        this.journal = new JournalVentes();
        this.compteurTransaction = 1;
    }

    public List<Boisson> consulterBoissons() {
        return stock.getBoissons().stream()
                .filter(b -> b.getQuantite() > 0)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public Transaction acheterBoisson(String nomBoisson, double montant) {
        String transactionId = "TXN" + String.format("%04d", compteurTransaction++);

        if (montant <= 0) {
            Transaction transaction = new Transaction(transactionId, null, montant);
            transaction.setStatut(StatutTransaction.ECHEC_MONTANT_INVALIDE);
            transaction.setMessageErreur("Le montant doit être positif");
            journal.ajouterTransaction(transaction);
            return transaction;
        }

        Boisson boisson = stock.rechercherBoisson(nomBoisson);
        if (boisson == null) {
            Transaction transaction = new Transaction(transactionId, null, montant);
            transaction.setStatut(StatutTransaction.ECHEC_BOISSON_INEXISTANTE);
            transaction.setMessageErreur("Boisson non trouvée");
            journal.ajouterTransaction(transaction);
            return transaction;
        }

        if (!stock.estBoissonDisponible(nomBoisson)) {
            Transaction transaction = new Transaction(transactionId, boisson, montant);
            transaction.setStatut(StatutTransaction.ECHEC_RUPTURE_STOCK);
            transaction.setMessageErreur("Boisson en rupture de stock");
            journal.ajouterTransaction(transaction);
            return transaction;
        }

        if (montant < boisson.getPrix()) {
            Transaction transaction = new Transaction(transactionId, boisson, montant);
            transaction.setStatut(StatutTransaction.ECHEC_MONTANT_INSUFFISANT);
            transaction.setMessageErreur(String.format("Montant insuffisant. Prix: %.0f F CFA", boisson.getPrix()));
            journal.ajouterTransaction(transaction);
            return transaction;
        }

        stock.reduireQuantite(nomBoisson, 1);
        Transaction transaction = new Transaction(transactionId, boisson, montant);
        transaction.setMontantRendu(transaction.calculerMontantRendu());
        journal.ajouterTransaction(transaction);

        return transaction;
    }

    public boolean rechargerStock(String nomBoisson, int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive");
        }

        Boisson boisson = stock.rechercherBoisson(nomBoisson);
        if (boisson != null) {
            if (stock.peutAjouterQuantite(nomBoisson, quantite)) {
                boisson.setQuantite(boisson.getQuantite() + quantite);
                return true;
            }
        }
        return false;
    }

    public boolean ajouterNouvelleBoisson(String nom, double prix, int quantite) {
        if (stock.rechercherBoisson(nom) != null) {
            return false; // Boisson déjà existante
        }

        Boisson nouvelleBoisson = new Boisson(nom, prix, quantite);
        stock.ajouterBoisson(nouvelleBoisson);
        return true;
    }

    public double insererMontant(Utilisateur utilisateur, double montant) {
        try {
            utilisateur.getPortefeuille().insererMontant(montant);
            return utilisateur.getPortefeuille().getMontant();
        } catch (IllegalArgumentException e) {
            return -1; // Erreur
        }
    }

    public double rendreMonnaie(Utilisateur utilisateur) {
        double montant = utilisateur.getPortefeuille().getMontant();
        utilisateur.getPortefeuille().viderPortefeuille();
        return montant;
    }

    public Stock obtenirStock() {
        return stock;
    }

    public boolean estBoissonDisponible(String nomBoisson) {
        return stock.estBoissonDisponible(nomBoisson);
    }

    public double calculerMontantRendu(String nomBoisson, double montantPaye) {
        Boisson boisson = stock.rechercherBoisson(nomBoisson);
        if (boisson != null && montantPaye >= boisson.getPrix()) {
            return montantPaye - boisson.getPrix();
        }
        return 0.0;
    }

    public void initialiserDistributeur() {
        // Prix en Franc CFA
        stock.ajouterBoisson(new Boisson("Coca-Cola", 500, 10)); // 500 F CFA
        stock.ajouterBoisson(new Boisson("Pepsi", 500, 8));
        stock.ajouterBoisson(new Boisson("Fanta", 450, 12));
        stock.ajouterBoisson(new Boisson("Sprite", 450, 15));
        stock.ajouterBoisson(new Boisson("Eau", 300, 20));
    }

    public JournalVentes getJournal() {
        return journal;
    }
}
