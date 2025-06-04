package org.example;

public enum StatutTransaction {
    REUSSIE,
    ECHEC_MONTANT_INSUFFISANT,
    ECHEC_RUPTURE_STOCK,
    ECHEC_BOISSON_INEXISTANTE,
    ECHEC_MONTANT_INVALIDE;

    public boolean estReussie() {
        return this == StatutTransaction.REUSSIE;
    }
}

