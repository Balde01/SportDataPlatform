# Diagramme C – Gouvernance / Audit (Phase 3)

## 1. Objectif
Ce diagramme représente la **couche de gouvernance et de supervision**.

Il permet :
- de tracer les actions administratives
- de responsabiliser les ADMIN
- de sécuriser la plateforme à long terme

---

## 2. Périmètre

### Inclus
- Journalisation des actions ADMIN
- Supervision des opérations sensibles
- Bootstrap du SUPER_ADMIN

### Exclus
- Authentification
- Gestion des agents
- Logique métier sportive

---

## 3. Audit

### AdminAuditLog
Représente une action administrative :
- qui agit
- sur qui
- quelle action
- quand
- avec quel contexte

Ces données sont essentielles pour la traçabilité.

---

## 4. AuditService
Service responsable de :
- enregistrer les actions sensibles
- garantir l’intégrité des journaux

Aucune logique métier ne dépend de l’audit.

---

## 5. Super Admin

### AdminBootstrapper
Permet d’injecter un SUPER_ADMIN :
- au démarrage
- sans inscription publique
- via configuration sécurisée

---

## 6. Philosophie
- Responsabilité et traçabilité
- Sécurité par conception
- Gouvernance découplée du métier
