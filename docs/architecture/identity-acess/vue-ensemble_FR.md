# Architecture Finale – Vue d’ensemble (SportDataAuth)

## Objectif de ce document
Ce document présente la **vue d’ensemble finale** de l’architecture SportDataAuth.
Il ne remplace pas les diagrammes détaillés (A, B, C), mais sert de **point d’entrée global** pour comprendre le système.

L’objectif est la **lisibilité**, pas le détail technique.

---

## Pourquoi une architecture découpée
Le système est volontairement séparé en **trois sous-systèmes indépendants** :

- **A – Identity / Auth (MVP)** : gestion des comptes et de l’authentification
- **B – Admin / Provisioning** : gestion administrative des agents
- **C – Governance / Audit** : supervision et traçabilité

Ce découpage évite :
- les diagrammes illisibles
- le couplage excessif
- la complexité prématurée

---

## A) Identity / Auth (MVP)
Responsable de :
- création de comptes CLIENT
- invitation et activation des AGENT
- gestion des mots de passe
- connexion et tokens
- rôles et statuts utilisateurs

C’est le **noyau fonctionnel minimal** du système.
Les autres parties dépendent de lui.

---

## B) Admin / Provisioning
Responsable de :
- accepter/provisionner les AGENT
- réémettre des invitations
- gérer les rôles
- activer/désactiver des comptes

Ce module :
- n’implémente pas l’authentification
- utilise les services du module A
- applique les règles métier administratives

---

## C) Governance / Audit
Responsable de :
- journaliser les actions administratives
- assurer la traçabilité
- injecter le SUPER_ADMIN au démarrage

Ce module :
- n’impacte pas la logique métier
- est optionnel au MVP
- renforce la sécurité long terme

---

## Flux global simplifié
1. Un CLIENT s’inscrit via A.
2. Un AGENT est accepté via B.
3. L’invitation est générée via A.
4. L’AGENT active son compte.
5. Les actions admin peuvent être auditées via C.

---


