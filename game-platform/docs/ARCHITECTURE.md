# Architektura — Etap 0

## Decyzja

Aplikacja jest modularnym monolitem Laravel.

```text
app/
├── Actions/
├── Domain/
│   ├── Catalog/
│   ├── Commerce/
│   ├── Publishing/
│   └── Support/
├── Enums/
├── Http/
│   ├── Controllers/
│   └── Requests/
├── Jobs/
├── Models/
├── Notifications/
├── Policies/
├── Providers/
└── Services/
```

## Granice modułów

### Catalog
Gry, gatunki, tagi, platformy, wydania, premiery i media katalogowe.

### Commerce
Produkty, ceny, koszyk, checkout, zamówienia, płatności, refundy i licencje.

### Publishing
Studia deweloperskie, zgłoszenia, pipeline, dokumenty, kamienie milowe i rozliczenia.

### Support
Baza wiedzy, tickety i komunikacja supportu.

## Warstwa HTTP

```text
routes/web.php        -> serwis publiczny
routes/admin.php      -> /admin
routes/developer.php  -> /developer
routes/api.php        -> /api/v1
routes/console.php    -> scheduler i komendy CLI
```

## RBAC

Etap 0 definiuje role administracyjne i bazową macierz permissions. Uprawnienia są egzekwowane przez Laravel Gates, a późniejsze operacje na rekordach będą dodatkowo chronione przez Policies.

## Storage

- `local` -> prywatny storage (`storage/app/private`);
- `public` -> materiały publiczne;
- buildy gier pozostają poza hostingiem współdzielonym i będą obsługiwane przez adapter storage / prywatne URL.

## Kolejki

Database queue na start. W środowisku współdzielonym scheduler może uruchamiać krótkie zadania `queue:work --stop-when-empty`.

## Płatności

Brak implementacji na Etapie 0. W Etapie 5 powstanie interfejs `PaymentGateway` i adapter sandbox. Dane kart nie będą przechowywane w aplikacji.
