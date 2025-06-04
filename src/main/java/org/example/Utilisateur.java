package org.example;

public class Utilisateur {
    private String id;
    private String nom;
    private boolean estPersonnel;
    private Portefeuille portefeuille;

    public Utilisateur(String id, String nom, boolean estPersonnel) {
        this.id = id;
        this.nom = nom;
        this.estPersonnel = estPersonnel;
        this.portefeuille = new Portefeuille();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public boolean estPersonnel() {
        return estPersonnel;
    }

    public Portefeuille getPortefeuille() {
        return portefeuille;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setEstPersonnel(boolean estPersonnel) {
        this.estPersonnel = estPersonnel;
    }

    public boolean rechargerStock(Stock stock, String nomBoisson, int quantite) {
        if (!estPersonnel) {
            return false;
        }

        Boisson boisson = stock.rechercherBoisson(nomBoisson);
        if (boisson != null) {
            boisson.setQuantite(boisson.getQuantite() + quantite);
            return true;
        }
        return false;
    }
}


