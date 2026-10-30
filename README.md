# TP1 SOA – Modélisation XML d'une agence de location de voitures

**Matière :** Architecture SOA et services web – LSI3 – FSS 2025-2026

**Étudiant(e) :** Amara Maymana

## 1. Domaine choisi
Gestion d'une agence de location de voitures (« Sfax Auto Location »). L'agence possède un parc de voitures, des clients, et enregistre les locations effectuées.

## 2. Structure du document XML (`agence.xml`)
```
agence (nom, ville)
├── voitures
│   └── voiture (id, disponible) : marque, modele, annee, categorie, prixJour, promotion
├── clients
│   └── client (id, actif) : nom, prenom, telephone, email
└── locations
    └── location (id, voitureRef, clientRef) : dateDebut, dateFin, montantTotal, statut
```

## 3. Choix de modélisation
- **Éléments** pour les données descriptives (marque, modèle, prix...), **attributs** pour les identifiants et les états (`id`, `disponible`, `actif`).
- `id` de type `xs:ID` et `voitureRef` / `clientRef` de type `xs:IDREF` : le validateur vérifie que chaque location référence une voiture et un client existants (intégrité référentielle).
- Types restreints : `categorie` et `statut` (énumérations), `prixJour` > 0, `annee` entre 2000 et 2030, `telephone` = 8 chiffres, `email` (pattern), dates en `xs:date`.
- Booléens (`disponible`, `actif`, `promotion`) pour faciliter les filtres XPath.

## 4. Schéma XSD (`agence.xsd`)
Types simples (`categorieType`, `statutType`, `prixType`, `anneeType`, `telephoneType`, `emailType`) et types complexes (`voitureType`, `clientType`, `locationType`) réutilisables, puis élément racine `agence`.

## 5. Implémentation Java (`Main.java`)
1. **Validation** : `SchemaFactory` -> `Schema` ->`Validator.validate(StreamSource)`.
2. **XPath** : `DocumentBuilder` pour parser, puis `XPathFactory` ->`XPath.compile()` -> `XPathExpression.evaluate()`.

## 6. Requêtes XPath
| | Objectif | Requête |
|---|----------|---------|
| Q1 | Toutes les marques | `voiture/marque` |
| Q2 | Voitures disponibles | `voiture[@disponible='true']/modele` |
| Q3 | Voitures en promotion | `voiture[promotion='true']/modele` |
| Q4 | Clients actifs | `client[@actif='true']/nom` |
| Q5 | Prix/jour > 100 DT | `voiture[prixJour > 100]/modele` |
| Q6 | Locations en cours | `location[statut='en_cours']/@id` |
| Q7 | Nombre de voitures | `count(voiture)` |
| Q8 | Chiffre d'affaires | `sum(location/montantTotal)` |

## 7. Résultats d'exécution
```
OK : le document XML est valide par rapport au schéma XSD.
Q1: Renault, Peugeot, Volkswagen, Mercedes
Q2: Clio 5, Golf 8, Classe C
Q3: 208, Golf 8
Q4: Ben Ali, Trabelsi
Q5: Golf 8, Classe C
Q6: L1
Q7: 4
Q8: 560.0 DT
```
