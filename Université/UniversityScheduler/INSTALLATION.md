# Instructions d'Installation - University Scheduler

## ✅ Prérequis Vérifiés

- ✓ Java JDK 11+ installé
- ✓ Maven 3.6+ installé
- ✓ Git (optionnel)
- ✓ 500 MB d'espace disque libre

---

## 📥 Étape 1 : Vérifier les Prérequis

Ouvrir un terminal PowerShell et vérifier :

```powershell
# Vérifier Java
java -version
# Output devrait afficher : Java 11.x.x ou supérieur

# Vérifier Maven
mvn -version
# Output devrait afficher : Apache Maven 3.6+ ou supérieur
```

---

## 📂 Étape 2 : Naviguer vers le Projet

```powershell
cd "c:\Users\LENOVO\Documents\Université\UniversityScheduler"
```

---

## 🔨 Étape 3 : Compiler le Projet

### Compilation Simple

```powershell
mvn clean compile
```

Cette commande :
- Nettoie les builds précédents (`clean`)
- Compile le code Java (`compile`)
- Télécharge les dépendances automatiquement

**Temps attendu** : 2-5 minutes (plus long la première fois)

### Si Erreur de Compilation

```powershell
# Mettre à jour Maven
mvn -U clean compile

# Ou forcer le téléchargement
mvn clean compile -U
```

---

## 📦 Étape 4 : Créer le JAR Exécutable

```powershell
mvn clean package
```

Cette commande :
- Compile le code
- Exécute les tests (s'il y en a)
- Crée un JAR exécutable

**Output attendu** :
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX s
[INFO] Finished at: ...
```

**Fichier créé** : `target\UniversityScheduler.jar`

---

## 🚀 Étape 5 : Lancer l'Application

### Méthode 1 : Avec Maven (Recommandé)

```powershell
mvn javafx:run
```

**Avantages** :
- Classpath automatique
- Débogage facile
- Redémarrage rapide

### Méthode 2 : JAR Direct

```powershell
java -jar target/UniversityScheduler.jar
```

### Méthode 3 : Avec Plus de Mémoire

```powershell
java -Xmx2G -Xms1G -jar target/UniversityScheduler.jar
```

---

## 🔐 Étape 6 : Premier Lancement - Se Connecter

Utiliser l'un des comptes de test :

### Admin
- **Email** : admin@university.edu
- **Mot de passe** : admin123

### Gestionnaire d'Emploi du Temps
- **Email** : manager@university.edu
- **Mot de passe** : pass123

### Enseignant
- **Email** : ndiaye@university.edu
- **Mot de passe** : pass123

### Étudiant
- **Email** : student1@university.edu
- **Mot de passe** : pass123

---

## ⚙️ Configuration Avancée

### Changer le Port de la Base de Données (si local)

Éditer `src/main/java/com/university/scheduler/dao/DatabaseManager.java` :

```java
// Ligne : private static final String DB_URL = "jdbc:sqlite:scheduler.db";
// Changer le chemin si besoin :
private static final String DB_URL = "jdbc:sqlite:C:/data/scheduler.db";
```

Puis recompiler :

```powershell
mvn clean compile
```

### Intégration IDE

#### IntelliJ IDEA

1. File → Open → sélectionner le dossier UniversityScheduler
2. Laisser Maven configurer le projet
3. Run → Run 'SchedulerApplication'

#### Eclipse

1. File → Import → Existing Maven Projects
2. Sélectionner UniversityScheduler
3. Right-click → Run As → Maven build...
4. Goals : `javafx:run`

#### VS Code

1. Installer extensions : "Extension Pack for Java"
2. Ouvrir le dossier UniversityScheduler
3. Terminal → Run Task → select "mvn javafx:run"

---

## 🛠️ Compilation Personnalisée

### Compiler un Module Spécifique

```powershell
# Compiler uniquement les modèles
mvn compile -pl :university-scheduler -am

# Compiler uniquement les tests
mvn test-compile
```

### Compiler avec Options Personnalisées

```powershell
# Avec logs verbeux
mvn clean compile -X

# Sauter les tests
mvn clean package -DskipTests

# Compiler pour Java 17
mvn compile -Dmaven.compiler.source=17 -Dmaven.compiler.target=17
```

---

## 📊 Vérifier la Structure Compilée

```powershell
# Voir la structure du JAR
jar tf target/UniversityScheduler.jar | head -20

# Voir la taille
ls -lh target/UniversityScheduler.jar
```

---

## 🧹 Nettoyage et Maintenance

### Nettoyer les Builds

```powershell
# Supprimer le dossier target
mvn clean

# Supprimer aussi la base de données locale
rm scheduler.db
```

### Réinitialiser le Projet

```powershell
# Nettoyer + supprimer cache Maven (optionnel)
mvn clean
rm -r ~/.m2/repository/com/university  # Sur Linux/Mac
```

### Réinstaller les Dépendances

```powershell
mvn dependency:resolve
mvn dependency:tree  # Voir l'arbre de dépendances
```

---

## 🔍 Dépannage de Compilation

### Erreur : "Java compiler not found"

**Solution** :
```powershell
# Définir JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
mvn clean compile
```

### Erreur : "Cannot find symbol"

**Solution** :
```powershell
# Nettoyer et recompiler
mvn clean compile -U

# Ou mettre à jour les dépendances
mvn dependency:tree
```

### Erreur : "JavaFX not found"

**Solution** :
- Vérifier que JavaFX est dans pom.xml (déjà inclus)
- Réinstaller les dépendances :

```powershell
mvn dependency:purge-local-repository
mvn clean dependencies:resolve
```

### Erreur : "Port déjà utilisé"

**Solution** :
- SQLite n'utilise pas de port réseau
- Vérifier que pas un autre processus n'accède à scheduler.db :

```powershell
# Trouver le processus
tasklist | findstr java

# Tuer si nécessaire
taskkill /PID xxxx /F
```

---

## ✅ Vérification de la Installation

Une installation réussie affiche :

```
1. ✓ Compilation sans erreur
2. ✓ JAR créé dans target/
3. ✓ Fenêtre de login s'ouvre
4. ✓ Connexion réussie avec un compte de test
5. ✓ Accès au dashboard correspondant au rôle
6. ✓ Base de données scheduler.db créée
```

---

## 📝 Fichiers Importants Après Build

```
UniversityScheduler/
├── pom.xml                          # Configuration Maven
├── README.md                         # Documentation principale
├── INSTALLATION.md                  # Ce fichier
├── target/
│   ├── UniversityScheduler.jar      # JAR exécutable  ⭐
│   ├── classes/                     # Classes compilées
│   └── dependencies/                # Dépendances JAR
├── scheduler.db                     # Base de données SQLite ⭐
└── src/                             # Code source
```

---

## 🚀 Lancement en Production

Pour un déploiement :

```powershell
# Créer un dossier d'installation
mkdir "C:\Program Files\UniversityScheduler"
copy target\UniversityScheduler.jar "C:\Program Files\UniversityScheduler\"

# Lancer depuis n'importe quel dossier
cd "C:\Program Files\UniversityScheduler"
java -jar UniversityScheduler.jar
```

---

## 📞 Support Installation

En cas de problème :

1. Vérifier les **logs**
2. Consulter les **Prérequis** 
3. Nettoyer et **Recompiler**
4. Supprimer la **base de données** et redémarrer
5. Vérifier les **versions** (Java, Maven)

**Contact** : Se référer au README.md principal

---

**Dernière mise à jour** : Mars 2026  
**Version** : 1.0.0
