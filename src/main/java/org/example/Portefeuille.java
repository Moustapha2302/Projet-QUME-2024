package org.example;

import java.util.*;

public class Portefeuille {
    private double montantDisponible;
    private List<Double> historiqueInsertions;

    public Portefeuille() {
        this.montantDisponible = 0.0;
        this.historiqueInsertions = new ArrayList<>();
    }

    public void insererMontant(double montant) throws IllegalArgumentException {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        this.montantDisponible += montant;
        this.historiqueInsertions.add(montant);
    }

    public double getMontant() {
        return montantDisponible;
    }

    public double retournerMontant(double montant) {
        if (montant <= montantDisponible) {
            montantDisponible -= montant;
            return montant;
        }
        double montantRetourne = montantDisponible;
        montantDisponible = 0.0;
        return montantRetourne;
    }

    public boolean peutAcheter(double prix) {
        return montantDisponible >= prix;
    }

    public void viderPortefeuille() {
        montantDisponible = 0.0;
    }

    public List<Double> getHistorique() {
        return new ArrayList<>(historiqueInsertions);
    }
}


