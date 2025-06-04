package org.example;

import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private LocalDateTime date;
    private Boisson boisson;
    private double montant;
    private StatutTransaction statut;
    private double montantRendu;
    private String messageErreur;

    public Transaction(String id, Boisson boisson, double montant) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.boisson = boisson;
        this.montant = montant;
        this.statut = StatutTransaction.REUSSIE;
        this.montantRendu = 0.0;
        this.messageErreur = "";
    }

    // Getters
    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Boisson getBoisson() {
        return boisson;
    }

    public double getMontant() {
        return montant;
    }

    public StatutTransaction getStatut() {
        return statut;
    }

    public double getMontantRendu() {
        return montantRendu;
    }

    public String getMessageErreur() {
        return messageErreur;
    }

    // Setters
    public void setStatut(StatutTransaction statut) {
        this.statut = statut;
    }

    public void setMontantRendu(double montantRendu) {
        this.montantRendu = montantRendu;
    }

    public void setMessageErreur(String messageErreur) {
        this.messageErreur = messageErreur;
    }

    // Détails formatés de la transaction
    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Transaction ID: ").append(id)
                .append("\nDate: ").append(date)
                .append("\nBoisson: ").append(boisson != null ? boisson.getNom() : "N/A")
                .append("\nMontant payé: ").append(String.format("%.0f F CFA", montant))
                .append("\nStatut: ").append(statut)
                .append("\nMontant rendu: ").append(String.format("%.0f F CFA", montantRendu));

        if (!messageErreur.isEmpty()) {
            details.append("\nErreur: ").append(messageErreur);
        }

        return details.toString();
    }

    public boolean estReussie() {
        return statut != null && statut.estReussie();
    }

    public double calculerMontantRendu() {
        if (boisson != null && montant >= boisson.getPrix()) {
            return montant - boisson.getPrix();
        }
        return 0.0;
    }
}
