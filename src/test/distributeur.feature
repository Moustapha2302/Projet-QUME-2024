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
