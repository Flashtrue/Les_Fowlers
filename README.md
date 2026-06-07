# Les Fowlers – CAFHEG (Caisse d'Allocations Familiales HEG)

**Groupe :** Les Fowlers  
**Repo :** https://github.com/Flashtrue/Les_Fowlers

---

## Description du projet

Application Spring Boot de gestion des allocations familiales. Elle expose une API REST permettant de gérer les allocataires, de déterminer quel parent a droit à l'allocation, et de générer des exports PDF des versements.

---

## Exercices réalisés

### Exercice 1 – Prise en main, Tests & Refactoring

- Prise en connaissance du projet et de ses interactions entre classes
- Test du endpoint `/droits/quel-parent` via un outil de test REST
- **Partie 2** : Harnais de tests JUnit 5 à 100% de couverture sur `AllocationService#getParentDroitAllocation`
- **Partie 3** : Refactoring TDD — remplacement de la `Map<String, Object>` par une classe dédiée (`DroitAllocationRequest`), sans casser l'API REST
- **Partie 4** : Implémentation complète de la logique métier selon les règles officielles des allocations familiales (lien eak.admin.ch) :
  - Si un seul parent travaille → il perçoit l'allocation
  - Si les deux travaillent → celui avec le salaire le plus élevé la perçoit
  - Si aucun ne travaille → celui avec le revenu le plus élevé la perçoit

---

### Exercice 2 – CRUD Allocataires

- **Partie 1 – Suppression** : service de suppression d'un allocataire avec contrainte — impossible de supprimer un allocataire qui possède déjà des versements
- **Partie 2 – Modification** : service de mise à jour du nom et prénom ; le numéro AVS ne peut pas être modifié ; la modification n'est effectuée que si au moins un des deux champs a changé
- **Partie 3 – Exposition REST** : endpoints exposés et testés :
  - `DELETE /allocataires/{id}`
  - `PUT /allocataires/{id}`

---

### Exercice 3 – Non réalisé

---

### Exercice 4 – Logging (SLF4J / Logback)

Remplacement de tous les `System.out.println` et `printStackTrace` par des loggers SLF4J avec les niveaux appropriés :

| Niveau  | Usage |
|---------|-------|
| `error` | Exceptions (avec la cause passée au logger) |
| `warn`  | Problèmes non critiques |
| `info`  | Accès aux services |
| `debug` | Informations techniques |
| `trace` | Détails fins |

Configuration Logback (`logback.xml`) avec trois appenders :

- **`err.log`** : niveau `ERROR` pour tous les packages `ch.*`
- **`cafheg_{date-jour}.log`** : niveau `INFO` pour les packages des services (rolling daily)
- **Console** : niveau `DEBUG` pour tous les packages

---

### Exercice 5 – Tests d'intégration (DBUnit / JUnit 5)

- Réorganisation du projet avec un répertoire `src/integration-test/` séparé des tests unitaires
- Classe `MyTestsIT` dans `src/integration-test/java/`
- Tests d'intégration avec DBUnit + H2 (base en mémoire) + AssertJ couvrant :
  - Suppression d'un allocataire
  - Modification d'un allocataire
- Jeu de données de test déclaré dans `src/integration-test/resources/dataset.xml`

---

## Stack technique

- **Java 25** / **Spring Boot 4**
- **PostgreSQL 18** via Docker Compose
- **Spring JDBC** + **Flyway** pour les migrations
- **Apache PDFBox** pour les exports PDF
- **SLF4J / Logback** pour le logging
- **JUnit 5**, **Mockito**, **DBUnit**, **H2**, **AssertJ** pour les tests
- **Springdoc OpenAPI** — Swagger UI disponible à `http://localhost:8080/api/swagger-ui/index.html`
