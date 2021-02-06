# Filmrunner

Application de bibliothèque de films avec Spring Boot et Angular intégré

Angular 11
Angular Material
Spring Security avec authentification JWT
Base de données H2
Lecture et écriture de fichiers XLS avec jxl

## Installation

Configurer l'environnement de développement Angular.<br>
Ou bien modifier le fichier src/main/resources/front/frontend/src/environments/environment.ts

```javascript
export const environment = {
  production: false,
  baseUrl: 'http://localhost:8080/'
};
```

## Usage

```bash
http://localhost:4200
```

Les fichiers excel doivent avoir un formatage prédéfini pour pouvoir être importés

## Demo

```bash
https://filmrunner.herokuapp.com/
```