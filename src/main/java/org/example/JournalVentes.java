package org.example;

import java.util.*;
import java.time.LocalDateTime;

public class JournalVentes {
    private List<Transaction> transactions;

    public JournalVentes() {
        this.transactions = new ArrayList<>();
    }

    public void ajouterTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getHistorique() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getTransactionsParDate(LocalDateTime debut, LocalDateTime fin) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(debut) && !t.getDate().isAfter(fin))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public double getChiffreAffaires() {
        return transactions.stream()
                .filter(Transaction::estReussie)
                .mapToDouble(t -> t.getBoisson().getPrix())
                .sum();
    }

    public Map<String, Integer> getStatistiquesVentes() {
        Map<String, Integer> stats = new HashMap<>();
        transactions.stream()
                .filter(Transaction::estReussie)
                .forEach(t -> {
                    String nom = t.getBoisson().getNom();
                    stats.put(nom, stats.getOrDefault(nom, 0) + 1);
                });
        return stats;
    }
}

