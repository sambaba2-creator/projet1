# University Scheduler

## 🎓 Système de Gestion des Salles et Emplois du Temps Universitaires

**University Scheduler** est une application complète pour centraliser, automatiser et optimiser la gestion des salles et des emplois du temps dans une université.

---

## 📋 Table des Matières

1. [Caractéristiques](#caractéristiques)
2. [Prérequis](#prérequis)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Utilisation](#utilisation)
6. [Architecture](#architecture)
7. [Technologies](#technologies)
8. [Guide Utilisateur](#guide-utilisateur)
9. [Dépannage](#dépannage)

---

## ✨ Caractéristiques

### Fonctionnalités Principales

- ✅ **Gestion des Utilisateurs** : Création, modification et suppression d'utilisateurs
- ✅ **Gestion des Bâtiments et Salles** : Configuration des infrastructures
- ✅ **Gestion des Équipements** : Attribution des équipements aux salles
- ✅ **Création de Cours** : Planification des cours avec détection automatique des conflits
- ✅ **Détection Intelligente des Conflits** :
  - Conflits de salles (double réservation)
  - Conflits d'enseignants (deux cours simultanés)
  - Conflits de classes (double cours)
- ✅ **Recherche de Salles** : Recherche en temps réel avec critères avancés
- ✅ **Réservation Ponctuelles** : Réservation de salles pour événements spéciaux
- ✅ **Statistiques et Rapports** : Dashboard avec KPIs et analyses
- ✅ **Notifications** : Alertes et notifications de changements
- ✅ **Gestion par Rôles** : 4 rôles différents (Admin, Manager, Enseignant, Étudiant)

### Rôles Utilisateurs

| Rôle | Fonctionnalités |
|------|-----------------|
| **Administrateur** | Gérer les utilisateurs, bâtiments, salles, équipements, consulter statistiques |
| **Gestionnaire d'emploi du temps** | Créer/modifier/supprimer cours, assigner salles, résoudre conflits |
| **Enseignant** | Consulter emploi du temps, réserver salles ponctuelles, signaler problèmes |
| **Étudiant** | Consulter emploi du temps de sa classe, rechercher salles libres |

---

## 🔧 Prérequis

- **Java** : JDK 11 ou supérieur
- **Maven** : 3.6.0 ou supérieur
- **SQLite** : Intégré (sqlite-jdbc 3.44.0.0)
- **OS** : Windows, macOS, Linux
- **RAM** : 2 GB minimum

### Vérifier les Prérequis

```bash
java -version
mvn -version
```

---

## 📦 Installation

### 1. Cloner ou Télécharger le Projet

```bash
cd c:\Users\LENOVO\Documents\Université\UniversityScheduler
```

### 2. Compiler le Projet

```bash
mvn clean compile
```

### 3. Créer le JAR Exécutable

```bash
mvn clean package
```

Le JAR sera créé dans le dossier `target/` avec le nom `UniversityScheduler.jar`

### 4. Exécuter l'Application

#### Avec Maven :
```bash
mvn javafx:run
```

#### Directement le JAR :
```bash
java -jar target/UniversityScheduler.jar
```

---

## ⚙️ Configuration

### Base de Données

La base de données SQLite se crée automatiquement au premier lancement dans le répertoire courant avec le nom `scheduler.db`.

### Initialisation de la Base

Le script SQL (`src/main/resources/db/init.sql`) s'exécute automatiquement au démarrage si c'est la première fois.

**Tables créées automatiquement** :
- `users` - Utilisateurs du système
- `buildings` - Bâtiments
- `rooms` - Salles
- `equipments` - Équipements
- `room_equipment` - Association salle-équipement
- `classes` - Classes d'étudiants
- `courses` - Cours
- `reservations` - Réservations
- `notifications` - Notifications
- `conflict_logs` - Journal des conflits
- `usage_statistics` - Statistiques d'utilisation

### Données d'Exemple

Des données d'exemple sont incluses dans le script SQL initial.

---

## 🚀 Utilisation

### Premier Lancement

1. Ouvrir l'application
2. Se connecter avec les identifiants de test fournis
3. Configurer les bâtiments et salles (Admin)
4. Créer les cours (Gestionnaire)
5. Consulter et réserver des salles (Enseignants/Étudiants)

### Identifiants de Test

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | admin@university.edu | admin123 |
| **Gestionnaire EDT** | **manager@university.edu** | **manager123** |
| Enseignant 1 | ndiaye@university.edu | pass123 |
| Étudiant 1 | student1@university.edu | pass123 |

---

## 🏗️ Architecture

### Structure des Dossiers

```
UniversityScheduler/
├── src/main/
│   ├── java/com/university/scheduler/
│   │   ├── SchedulerApplication.java          # Point d'entrée
│   │   ├── model/                              # Classes modèles
│   │   │   ├── User.java
│   │   │   ├── Course.java
│   │   │   ├── Room.java
│   │   │   ├── Building.java
│   │   │   ├── Equipment.java
│   │   │   ├── Class.java
│   │   │   └── Reservation.java
│   │   ├── dao/                                # Accès aux données
│   │   │   ├── DatabaseManager.java
│   │   │   ├── UserDAO.java
│   │   │   ├── CourseDAO.java
│   │   │   ├── RoomDAO.java
│   │   │   ├── BuildingDAO.java
│   │   │   ├── EquipmentDAO.java
│   │   │   ├── ClassDAO.java
│   │   │   └── ReservationDAO.java
│   │   ├── business/                           # Logique métier
│   │   │   ├── ConflictDetectionService.java
│   │   │   ├── RoomSearchService.java
│   │   │   ├── StatisticsService.java
│   │   │   └── NotificationService.java
│   │   └── ui/controller/                      # Contrôleurs JavaFX
│   │       ├── LoginController.java
│   │       ├── AdminDashboardController.java
│   │       ├── ManagerDashboardController.java
│   │       ├── TeacherDashboardController.java
│   │       └── StudentDashboardController.java
│   └── resources/
│       ├── fxml/                               # Fichiers FXML
│       │   ├── login.fxml
│       │   ├── admin_dashboard.fxml
│       │   ├── manager_dashboard.fxml
│       │   ├── teacher_dashboard.fxml
│       │   └── student_dashboard.fxml
│       ├── css/                                # Feuilles de style
│       │   └── styles.css
│       └── db/                                 # Scripts SQL
│           └── init.sql
├── pom.xml                                      # Configuration Maven
└── README.md                                    # Documentation
```

### Design Pattern Utilisés

- **DAO Pattern** : Séparation entre la logique métier et l'accès aux données
- **Singleton** : DatabaseManager
- **Observer** : Notifications
- **Strategy** : Détection des conflits
- **MVC** : Séparation Model-View-Controller

---

## 🛠️ Technologies

### Backend
- **Langage** : Java 11+
- **Framework GUI** : JavaFX 17
- **Base de Données** : SQLite 3
- **Build Tool** : Maven 3.6+

### Dépendances Principales
- `javafx-controls` et `javafx-fxml` : Interface utilisateur
- `sqlite-jdbc` : Driver SQLite
- `slf4j` et `logback` : Logging
- `gson` : Traitement JSON (optionnel)
- `jakarta.mail` : Notifications email (optionnel)

---

## 👥 Guide Utilisateur

### 1. Administrateur

#### Tâche : Ajouter un bâtiment
1. Se connecter avec identifiants Admin
2. Aller dans l'onglet "Bâtiments"
3. Remplir les champs (Nom, Localisation, Étages)
4. Cliquer "Ajouter Bâtiment"

#### Tâche : Créer un utilisateur
1. Aller dans l'onglet "Utilisateurs"
2. Remplir les champs (Nom, Email, Mot de passe, Rôle)
3. Cliquer "Ajouter Utilisateur"

### 2. Gestionnaire d'Emploi du Temps

#### Tâche : Créer un cours
1. Aller dans l'onglet "Cours"
2. Sélectionner : Matière, Enseignant, Classe, Jour, Heure, Durée, Salle
3. Cliquer "Créer Cours"
4. Si un conflit est détecté, un message d'erreur s'affiche

#### Tâche : Rechercher une salle disponible
1. Aller dans l'onglet "Recherche de Salles"
2. Entrer les critères : Capacité min, Type, Date, Horaire
3. Cliquer "Rechercher"
4. Les salles disponibles s'affichent

### 3. Enseignant

#### Tâche : Consulter mon emploi du temps
1. Aller dans l'onglet "Mon Emploi du Temps"
2. Les cours sont affichés avec jour, heure et salle

#### Tâche : Réserver une salle
1. Aller dans l'onglet "Réserver une Salle"
2. Entrer les critères de recherche
3. Cliquer "Rechercher"
4. Sélectionner une salle
5. Cliquer "Réserver"

### 4. Étudiant

#### Tâche : Consulter l'emploi du temps de ma classe
1. Aller dans l'onglet "Mon Emploi du Temps"
2. Les cours de la classe sont affichés

#### Tâche : Rechercher une salle pour étudier
1. Aller dans l'onglet "Rechercher une Salle"
2. Entrer capacité, date, horaire
3. Cliquer "Rechercher"
4. Les salles libres s'affichent

---

## 🔍 Algorithme de Détection des Conflits

### Logique Principale

Un conflit est détecté quand :

```
Nouveau_Début < Existant_Fin  ET  Nouveau_Fin > Existant_Début
```

### Types de Conflits Détectés

1. **Conflit de Salle** :
   - Deux réservations/cours à la même heure dans la même salle

2. **Conflit d'Enseignant** :
   - Un enseignant avec deux cours le même jour à des heures qui se chevauchent

3. **Conflit de Classe** :
   - Une classe avec deux cours le même jour qui se chevauchent

---

## 📊 Statistiques et Rapports

### Métriques Disponibles

- **Taux d'Occupation** = (Heures utilisées / Heures disponibles) × 100
- **Salles Critiques** : Occupancy > 90%
- **Salles Sous-utilisées** : Occupancy < 20%
- **Charge Enseignant** : Nombre de cours et heures par semaine

### Export des Rapports

Les rapports peuvent être générés en :
- Format texte brut (copie-coller)
- Format tableau (exportable en CSV via Excel)

---

## 🐛 Dépannage

### Problème : L'application ne démarre pas

**Solution** :
```bash
# Vérifier que le JDK est installé
java -version

# Vérifier les logs
mvn javafx:run 2>&1 | tee debug.log
```

### Problème : Base de données introuvable

**Solution** :
```bash
# La base se crée automatiquement au premier lancement
# Si c'est un problème persistent, supprimer scheduler.db
rm scheduler.db

# Relancer l'application
```

### Problème : Conflit lors de la création d'un cours

**Solution** :
1. Vérifier que l'enseignant n'a pas un autre cours le même jour
2. Vérifier que la classe n'a pas un autre cours aux mêmes heures
3. Vérifier que la salle est disponible
4. Essayer une autre salle ou un autre créneau horaire

### Problème : Caractères accentués non affichés

**Solution** : Vérifier l'encodage UTF-8
```bash
# Dans pom.xml, vérifier :
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
```

### Problème : Application lente ou figée

**Solution** :
```bash
# Augmenter la mémoire JVM
java -Xmx2G -Xms1G -jar target/UniversityScheduler.jar
```

---

## 📝 Notes de Développement  

### Points d'Extension

1. **Notifications Email** :
   - Décommenter les imports dans `NotificationService.java`
   - Configurer SMTP dans une classe `EmailConfig`

2. **Export PDF** :
   - Ajouter dépendance : `itext7` ou `pdfbox`
   - Implémenter dans `StatisticsService`

3. **Sauvegarde Automatique** :
   - Ajouter `ScheduledExecutorService` dans `DatabaseManager`

4. **Multi-langue** :
   - Ajouter `ResourceBundle` pour i18n

---

## 📄 Licence

Ce projet est fourni à titre éducatif.

---

## 👨‍💻 Support

Pour toute question ou problème :
1. Consulter le fichier de log : `debug.log`
2. Vérifier la base de données : `scheduler.db`
3. Relancer l'application avec `mvn clean javafx:run`

---

## 🎯 Améliorations Futures

- [ ] Authentification LDAP/Active Directory
- [ ] API REST pour intégration externe
- [ ] Mobile app pour consultation
- [ ] Synchronisation avec Google Calendar/Outlook
- [ ] Planification IA automatique
- [ ] Dashboard en temps réel avec WebSocket
- [ ] Support multi-langue

---

**Version** : 1.0.0  
**Date** : Mars 2026  
**Auteur** : Équipe Développement
