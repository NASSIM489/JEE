# TP N°2-1 : Spring Cloud Config
## Externalisation et Centralisation de la configuration des micro-services (Use Case avec GitHub & Git Local)

Ce projet implémente l'ensemble du TP N°2-1 avec **Spring Boot 3 (v3.3.5)**, **Spring Cloud (2023.0.3)** et **Java 17**.

---

## 1. Structure du Projet

```
c:\Users\nassi\Desktop\Jee\
│
├── config-server-repo/                         # Dépôt Git contenant la configuration externalisée
│   ├── .git/
│   └── microservice-produits.properties       # Configurations externalisées (H2, JPA, mes-configs.limitDeProduits=3)
│
├── config-server/                              # Microservice Serveur de Configuration (Port 9101)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/mcommerce/configserver/
│       │   └── ConfigServerApplication.java    # @EnableConfigServer
│       └── resources/
│           └── application.properties          # Port 9101, lien vers le repo Git
│
├── microservice-produits/                      # Microservice Produits (Port 9001)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/mproduits/
│       │   ├── MproduitsApplication.java       # @SpringBootApplication & @EnableConfigurationProperties
│       │   ├── configurations/
│       │   │   └── ApplicationPropertiesConfiguration.java  # @ConfigurationProperties("mes-configs") & @RefreshScope
│       │   ├── controller/
│       │   │   └── ProductController.java      # Endpoints GET /Produits, /Produits/{id}, Actuator Health
│       │   ├── dao/
│       │   │   └── ProductDao.java             # JpaRepository<Product, Integer>
│       │   ├── exceptions/
│       │   │   └── ProductNotFoundException.java
│       │   ├── model/
│       │   │   └── Product.java                # Entité JPA Product
│       │   └── service/
│       │       ├── IServiceProduct.java        # Interface du service
│       │       └── ServiceProduct.java         # Implémentation & CommandLineRunner (peuplement initial de 12 produits)
│       └── resources/
│           ├── bootstrap.properties            # Connexion au Config Server (http://localhost:9101)
│           └── application.properties          # Historique avant externalisation
│
├── pom.xml                                     # POM parent Maven multi-modules
├── start-config-server.bat                     # Script de lancement rapide du Config Server
└── start-microservice-produits.bat             # Script de lancement rapide du Microservice Produits
```

---

## 2. Fonctionnement de l'Architecture

```
                                  +------------------------------------+
                                  |           Dépôt Git                |
                                  |   (Local ou GitHub distant)        |
                                  |  microservice-produits.properties  |
                                  |   mes-configs.limitDeProduits=3    |
                                  +-----------------+------------------+
                                                    ^
                                                    |
                                                    v
                                  +-----------------+------------------+
                                  |     Spring Cloud Config Server     |
                                  |            (Port 9101)             |
                                  |       @EnableConfigServer          |
                                  +-----------------+------------------+
                                                    ^
                                                    | Fetch config au démarrage
                                                    v
                                  +-----------------+------------------+
                                  |       Microservice Produits        |
                                  |            (Port 9001)             |
                                  |   @EnableConfigurationProperties   |
                                  |   @ConfigurationProperties         |
                                  |         @RefreshScope              |
                                  +------------------------------------+
```

1. **Dépôt Git (`config-server-repo`)** : Il centralise les propriétés sensibles ou dynamiques (`spring.datasource.*`, `spring.jpa.*`, `mes-configs.limitDeProduits=3`).
2. **Config Server (`config-server`)** : Démarre sur le port **9101**, lit l'historique et les fichiers de configuration du dépôt Git, et les expose sous forme d'API REST JSON.
3. **Microservice Produits (`microservice-produits`)** : Démarre sur le port **9001**. Dès le démarrage (`bootstrap.properties`), il contacte le Config Server à l'adresse `http://localhost:9101` pour récupérer ses paramètres de base de données et sa variable métier `limitDeProduits`.

---

## 3. Démarrage des Services

### Option A : En ligne de commande avec les scripts fournis
1. Ouvrez un premier terminal et lancez le serveur de configuration :
   ```cmd
   .\start-config-server.bat
   ```
   *(Attendre le message `Started ConfigServerApplication on port 9101`)*

2. Ouvrez un second terminal et lancez le microservice Produits :
   ```cmd
   .\start-microservice-produits.bat
   ```
   *(Vous verrez le log `Fetching config from server at : http://localhost:9101` puis `Started MproduitsApplication on port 9001`)*

### Option B : Dans IntelliJ IDEA
1. Ouvrez le dossier `c:\Users\nassi\Desktop\Jee` dans IntelliJ IDEA.
2. IntelliJ va reconnaître le projet multi-modules via le `pom.xml` racine.
3. Démarrez d'abord `ConfigServerApplication.java` (`config-server`).
4. Démarrez ensuite `MproduitsApplication.java` (`microservice-produits`).

---

## 4. Tests et Vérifications

### Test 1 : Vérifier l'API du Config Server
Accédez dans le navigateur ou avec `curl` / `Postman` à :
- **http://localhost:9101/microservice-produits/default**
- ou **http://localhost:9101/microservice-produits/default/master**

**Résultat attendu :**
Un document JSON contenant le profil `default`, le commit Git, et les propriétés de `microservice-produits.properties` dont `"mes-configs.limitDeProduits": "3"`.

### Test 2 : Liste des produits limitée par la configuration externalisée
- **GET http://localhost:9001/Produits**

**Résultat attendu :**
Exactement **3 produits** retournés (`titre1`, `titre2`, `titre3`), car le Config Server a injecté `mes-configs.limitDeProduits=3` dans la classe `@ConfigurationProperties` `ApplicationPropertiesConfiguration` :
```json
[
  { "id": 1, "titre": "titre1", "description": "description1", "image": null, "prix": 1.0 },
  { "id": 2, "titre": "titre2", "description": "description2", "image": null, "prix": 2.0 },
  { "id": 3, "titre": "titre3", "description": "description3", "image": null, "prix": 3.0 }
]
```

### Test 3 : Détail d'un produit
- **GET http://localhost:9001/Produits/1**
Retourne le produit 1.

### Test 4 : Actuator Health
- **GET http://localhost:9001/actuator/health**
Retourne le statut `UP` avec les détails de connexion à la base H2 et au serveur Config Server.

### Test 5 : Console H2
- **URL :** `http://localhost:9001/h2-console`
- **JDBC URL :** `jdbc:h2:mem:testdb`
- **User Name :** `sa`
- **Password :** *(vide)*

---

## 5. Rechargement Dynamique à Chaud (`@RefreshScope`)

Grâce à `@RefreshScope` et à l'Actuator Spring Boot, vous pouvez modifier la configuration dans Git et mettre à jour le microservice **sans redémarrer** :

1. Modifiez la valeur dans `config-server-repo\microservice-produits.properties` :
   ```properties
   mes-configs.limitDeProduits=5
   ```
2. Committez la modification dans Git :
   ```bash
   cd c:\Users\nassi\Desktop\Jee\config-server-repo
   git add microservice-produits.properties
   git commit -m "Passage de la limite a 5"
   ```
3. Envoyez une requête POST à l'Actuator de `microservice-produits` :
   ```bash
   curl -X POST http://localhost:9001/actuator/refresh -H "Content-Type: application/json"
   ```
   *(ou sous PowerShell : `Invoke-RestMethod -Uri "http://localhost:9001/actuator/refresh" -Method Post -ContentType "application/json"`)*
4. Rafraîchissez **http://localhost:9001/Produits** :
   Vous obtiendrez désormais **5 produits** instantanément sans aucun redémarrage !

---

## 6. Synchronisation avec GitHub (Option du Sujet, Étapes 6 & 7)

Par défaut, le serveur est configuré pour lire le dépôt Git local (`config-server-repo`), ce qui permet une exécution autonome sans dépendre de token ou de connexion réseau.

Pour synchroniser avec votre GitHub comme mentionné dans les pages 15-17 :
1. Créez un dépôt GitHub vide nommé `mcommerce-config-repo`.
2. Générez un Personal Access Token (PAT) sur GitHub : **Settings > Developer Settings > Personal access tokens > Tokens (classic)** avec le scope `repo`.
3. Poussez votre dépôt local :
   ```bash
   cd c:\Users\nassi\Desktop\Jee\config-server-repo
   git remote add origin https://github.com/<votre-user-github>/mcommerce-config-repo.git
   git branch -M master
   git push -u origin master
   ```
4. Dans `config-server/src/main/resources/application.properties`, commentez la ligne locale et décommentez l'URI GitHub :
   ```properties
   spring.cloud.config.server.git.uri=https://github.com/<votre-user-github>/mcommerce-config-repo.git
   spring.cloud.config.server.git.default-label=master
   ```
