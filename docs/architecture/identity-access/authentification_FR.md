# Architecture – Plateforme d’authentification (MVP)

## 1. Objectif du système
Cette architecture représente le **noyau d’authentification et d’identité** de la plateforme.
Elle couvre uniquement :
- la création de comptes CLIENT
- la création et l’activation contrôlée des comptes AGENT
- la gestion des mots de passe
- la connexion sécurisée
- la gestion des rôles et statuts

Il s’agit d’un **MVP fonctionnel**, pensé pour évoluer sans refonte.

---

## 2. Périmètre du diagramme
### Inclus
- Inscription CLIENT
- Provisionnement AGENT par invitation
- Activation AGENT par création de mot de passe
- Expiration des invitations (7 jours)
- Authentification par token
- Rôles et statuts

### Exclus volontairement
- Audit administratif
- Supervision avancée
- Collecte de données sportives
- Logique métier des matchs
- Interface utilisateur

---

## 3. Architecture générale
Architecture en couches :

DTO → Services → Repositories → Model  
                ↓  
             Util / Security

Chaque couche a une responsabilité claire.

---

## 4. Modèle de domaine
### User
- id (UUID)
- email
- passwordHash
- roles (Set<Role>)
- status
- failedAttempts
- createdAt
- lastLoginAt

Le modèle reste volontairement simple.

### InvitationToken
- usage unique
- durée limitée (7 jours)
- activation ou reset de mot de passe

---

## 5. Workflow AGENT
1. L’agent réussit la formation (hors système)
2. Compte AGENT créé avec status DISABLED
3. Invitation envoyée (valide 7 jours)
4. L’agent définit son mot de passe
5. Compte activé
6. Invitation expirée → réémission requise

---

## 6. Gestion du temps
Une classe `Clock` est utilisée comme source unique du temps afin de garantir :
- cohérence
- testabilité
- règles métier fiables

---

## 7. Philosophie
- séparation stricte des responsabilités
- MVP volontairement limité
- extensibilité sans sur‑ingénierie
