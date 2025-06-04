Feature: Achat de boisson

  Background:
    Given le distributeur est initialisé avec les boissons suivantes:
      | nom        | prix | quantite |
      | Coca-Cola  | 500  | 5        |
      | Pepsi      | 500  | 0        |
      | Fanta      | 450  | 10       |

  Scenario: Achat réussi d'une boisson avec montant exact
    Given un client dispose de 500 FCFA
    When le client achète "Coca-Cola" avec 500 FCFA
    Then la transaction est réussie
    And aucune monnaie n'est rendue
    And la quantité de "Coca-Cola" est diminuée de 1

  Scenario: Achat réussi avec monnaie rendue
    Given un client dispose de 600 FCFA
    When le client achète "Coca-Cola" avec 600 FCFA
    Then la transaction est réussie
    And la monnaie rendue est de 100 FCFA
    And la quantité de "Coca-Cola" est diminuée de 1

  Scenario: Échec d'achat avec montant insuffisant
    Given un client dispose de 400 FCFA
    When le client achète "Coca-Cola" avec 400 FCFA
    Then la transaction échoue avec le message "Montant insuffisant. Prix: 500 F CFA"
    And la quantité de "Coca-Cola" reste à 5

  Scenario: Échec d'achat avec boisson en rupture de stock
    Given un client dispose de 500 FCFA
    When le client achète "Pepsi" avec 500 FCFA
    Then la transaction échoue avec le message "Boisson en rupture de stock"
    And la quantité de "Pepsi" reste à 0

  Scenario: Échec d'achat avec boisson inexistante
    Given un client dispose de 500 FCFA
    When le client achète "Sprite" avec 500 FCFA
    Then la transaction échoue avec le message "Boisson non trouvée"

  Scenario: Achat réussi avec monnaie rendue importante
    Given un client dispose de 1000 FCFA
    When le client achète "Fanta" avec 1000 FCFA
    Then la transaction est réussie
    And la monnaie rendue est de 550 FCFA
    And la quantité de "Fanta" est diminuée de 1

  Scenario: Échec d'achat avec montant négatif
    Given un client dispose de -100 FCFA
    When le client achète "Coca-Cola" avec -100 FCFA
    Then la transaction échoue avec le message "Le montant doit être positif"
    And la quantité de "Coca-Cola" reste à 5

  Scenario: Échec d'achat avec montant zéro
    Given un client dispose de 0 FCFA
    When le client achète "Coca-Cola" avec 0 FCFA
    Then la transaction échoue avec le message "Le montant doit être positif"
    And la quantité de "Coca-Cola" reste à 5

  Scenario: Achat d'une boisson à quantité minimale (limite de stock)
    Given un client dispose de 500 FCFA
    And la quantité de "Coca-Cola" est de 2
    When le client achète "Coca-Cola" avec 500 FCFA
    Then la transaction est réussie
    And la quantité de "Coca-Cola" est diminuée de 1

  Scenario: Recharge de stock par personnel autorisé réussie
    Given un utilisateur est personnel autorisé
    When il recharge le stock de "Pepsi" avec 10 unités
    Then la quantité de "Pepsi" est augmentée de 10

  Scenario: Recharge de stock par utilisateur non autorisé échoue
    Given un utilisateur n'est pas personnel
    When il tente de recharger le stock de "Pepsi" avec 10 unités
    Then la recharge est refusée
    And la quantité de "Pepsi" reste à 0

  Scenario: Ajout d'une nouvelle boisson réussie
    Given un utilisateur est personnel autorisé
    When il ajoute une nouvelle boisson "Sprite" avec un prix de 400 FCFA et une quantité de 20
    Then la boisson "Sprite" est ajoutée avec une quantité de 20

  Scenario: Ajout d'une boisson déjà existante échoue
    Given un utilisateur est personnel autorisé
    When il tente d'ajouter une nouvelle boisson "Coca-Cola" avec un prix de 500 FCFA et une quantité de 10
    Then l'ajout est refusé car la boisson existe déjà

  Scenario: Consultation du stock retourne les boissons disponibles
    When le client consulte le stock
    Then la liste des boissons contient "Coca-Cola", "Fanta" et "Pepsi"

  Scenario: Calcul du chiffre d'affaires après ventes
    When on calcule le chiffre d'affaires
    Then le chiffre d'affaires est supérieur ou égal à 0

  Scenario: Rendu monnaie après achat
    Given un client dispose de 700 FCFA
    When le client achète "Fanta" avec 700 FCFA
    Then la monnaie rendue est de 250 FCFA

  Scenario: Achat sans avoir inséré d'argent échoue
    Given un client dispose de 0 FCFA
    When le client achète "Fanta" avec 0 FCFA
    Then la transaction échoue avec le message "Le montant doit être positif"
    And la quantité de "Fanta" reste à 10
