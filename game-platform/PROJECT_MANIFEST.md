# PROJECT MANIFEST

## Projekt

Profesjonalna strona studia gier + platforma wydawniczo-sprzedażowa.

## Status

**Etap 0 — UKOŃCZONY W KODZIE**

## Stack

- Laravel 13
- PHP 8.3+
- MariaDB
- Blade
- Tailwind CSS 4
- Alpine.js
- Vite
- Database queue

## Ukończone

- fundament Laravel 13;
- konfiguracja MariaDB przez sterownik `mysql`;
- `.env.example` bez sekretów;
- modularna struktura `app/Domain`;
- rozdzielony routing public/admin/developer/API;
- enumy ról i permissions;
- modele `Role`, `Permission` i relacje z `User`;
- migracje `roles`, `permissions`, `role_user`, `permission_role`;
- idempotentny `RolePermissionSeeder`;
- rejestracja Laravel Gates dla permissions;
- layouty błędów 404/419/429/500/503;
- health check `/up`;
- database queue;
- scheduler zgodny z hostingiem współdzielonym;
- lokalny workflow instalacji;
- test uruchomienia aplikacji i test seedera.

## Migracje

1. `0001_01_01_000000_create_users_table.php`
2. `0001_01_01_000001_create_cache_table.php`
3. `0001_01_01_000002_create_jobs_table.php`
4. `2026_08_23_000100_create_roles_and_permissions_tables.php`

## Role

- Super admin
- Administrator
- Redaktor
- Publisher manager
- Support agent
- Finance
- Analyst

## Komendy instalacji

```bash
cp .env.example .env
composer install
php artisan key:generate
php artisan migrate --seed
npm install
npm run build
php artisan test
php artisan serve
```

## Zmienne `.env` wymagające decyzji

- `STUDIO_NAME`
- `PLATFORM_DOMAIN`
- `PAYMENT_PROVIDER`
- docelowe dane MariaDB
- docelowe SMTP

## Otwarte decyzje

1. Ostateczna nazwa studia.
2. Domena produkcyjna.
3. Operator płatności.
4. Docelowy object storage dla buildów.

Brak odpowiedzi na powyższe nie blokuje Etapu 1.

## Świadome odroczenia

- auth gracza/dewelopera: Etap 4;
- 2FA: Etap 4;
- pełne Policies rekordowe: wraz z modułami, zanim pojawią się dane wrażliwe;
- katalog/gatunki: Etap 3;
- płatności: Etap 5;
- konto developerskie seedowane lokalnie: po wdrożeniu auth;
- finalna konfiguracja SEOHost: Etap 11.

## Następny etap

**Etap 1 — design system i layout.**
