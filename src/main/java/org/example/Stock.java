package org.example;
import java.util.*;

public class Stock {
    private List<Boisson> boissons;
    private int quantiteMinimale;
    private int quantiteMaximale;

    public Stock(int quantiteMinimale, int quantiteMaximale) {
        this.boissons = new ArrayList<>();
        this.quantiteMinimale = quantiteMinimale;
        this.quantiteMaximale = quantiteMaximale;
    }

    public void ajouterBoisson(Boisson boisson) {
        Boisson existante = rechercherBoisson(boisson.getNom());
        if (existante != null) {
            existante.setQuantite(existante.getQuantite() + boisson.getQuantite());
        } else {
            boissons.add(boisson);
        }
    }

    public boolean retirerBoisson(String nom, int quantite) {
        Boisson boisson = rechercherBoisson(nom);
        if (boisson != null && boisson.getQuantite() >= quantite) {
            boisson.setQuantite(boisson.getQuantite() - quantite);
            return true;
        }
        return false;
    }

    public void reduireQuantite(String nom, int quantite) {
        Boisson boisson = rechercherBoisson(nom);
        if (boisson != null) {
            boisson.setQuantite(Math.max(0, boisson.getQuantite() - quantite));
        }
    }

    public Boisson rechercherBoisson(String nom) {
        return boissons.stream()
                .filter(b -> b.getNom().equalsIgnoreCase(nom))
                .findFirst()
                .orElse(null);
    }

    public List<Boisson> getBoissons() {
        return new ArrayList<>(boissons);
    }

    public boolean estBoissonDisponible(String nom) {
        Boisson boisson = rechercherBoisson(nom);
        return boisson != null && boisson.getQuantite() > 0;
    }

    public boolean estEnRuptureDeStock(String nom) {
        Boisson boisson = rechercherBoisson(nom);
        return boisson == null || boisson.getQuantite() <= quantiteMinimale;
    }

    public boolean peutAjouterQuantite(String nom, int quantite) {
        Boisson boisson = rechercherBoisson(nom);
        if (boisson == null) return true;
        return boisson.getQuantite() + quantite <= quantiteMaximale;
    }

    public int getQuantiteMinimale() {
        return quantiteMinimale;
    }

    public int getQuantiteMaximale() {
        return quantiteMaximale;
    }
}


