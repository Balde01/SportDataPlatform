# Diagramme B – Administration / Provisionnement (Phase 2)

## 1. Objectif
Ce diagramme décrit la **gestion administrative des comptes AGENT**.
Il intervient **après** le noyau d’authentification (Diagramme A).

Son rôle est de permettre à des utilisateurs ADMIN :
- de créer des comptes AGENT
- d’envoyer des invitations
- de réémettre des invitations expirées
- de gérer les rôles et statuts

---

## 2. Périmètre

### Inclus
- Provisionnement d’agents
- Réémission d’invitations
- Gestion des rôles
- Activation / désactivation des comptes

### Exclus
- Authentification
- Inscription CLIENT
- Audit et supervision
- Collecte de données sportives

---

## 3. Service principal

### UserManagementService
Service central qui :
- vérifie les droits (via AuthorizationService)
- orchestre les actions administratives
- délègue l’activation aux invitations

Aucune logique d’authentification n’est présente ici.

---

## 4. Sécurité et autorisations

### AuthorizationService
Responsable de :
- vérifier qu’un utilisateur est ADMIN
- empêcher toute action non autorisée

Les règles d’accès sont centralisées pour éviter la duplication.

---

## 5. Dépendance avec le diagramme A
Ce diagramme dépend de :
- User
- UserRepository
- InvitationService

Il **n’implémente pas** l’invitation, mais l’utilise.

---

## 6. Philosophie
- Séparation claire entre identité et administration
- Aucun effet de bord sur l’authentification
- Extension naturelle du MVP sans complexifier le noyau
