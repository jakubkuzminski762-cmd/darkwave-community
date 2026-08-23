# PROJECT MANIFEST

## Projekt

Profesjonalna strona studia gier + platforma wydawniczo-sprzedażowa.

## Status

**Etap 1 — UKOŃCZONY W KODZIE**

Publiczny staging: `https://darkwave-community-production.up.railway.app`

## Stack

- Laravel 13
- PHP 8.3+
- MariaDB / MySQL-compatible staging
- Blade
- Tailwind CSS 4
- Alpine.js
- Vite
- Database queue

## Ukończone — Etap 0

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
- test uruchomienia aplikacji i test seedera;
- staging Railway z bazą MySQL i automatycznym deployem z brancha `game-platform`.

## Ukończone — Etap 1

- tokeny kolorów zgodne ze specyfikacją: bg, surface, elevated, text, muted, primary, secondary, danger, warning i success;
- spójny system promieni 8/14 px, spacing unit 4 px i content max 1280 px;
- typografia: display stack Space Grotesk/Manrope, UI Inter, identyfikatory JetBrains Mono z bezpiecznymi fallbackami systemowymi;
- responsywny publiczny layout;
- sticky header desktop;
- mobilne menu Alpine z `aria-expanded`, obsługą Escape i powrotem fokusu;
- footer wielokolumnowy;
- breadcrumbs;
- buttony primary, secondary, ghost, destructive, disabled i loading;
- pola formularzy z label, help text, licznikiem znaków, stanem błędu i `aria-describedby`;
- standardowe karty gier z lokalnym kolorem prezentacji;
- chipy, badge statusów i platform;
- alerty success/warning oraz neutralne;
- toast z `aria-live`;
- modal z `aria-modal`, Escape, blokadą scrolla, focus trap i przywracaniem fokusu;
- skeleton cards i shimmer;
- demonstracyjne stany UI;
- układ responsywny do 320 px;
- `prefers-reduced-motion: reduce` wyłączający animacje i transformacje;
- skip link i widoczne stany `focus-visible`;
- testy regresyjne podstawowej dostępności Etapu 1;
- brak logiki sklepu zgodnie z zakresem etapu.

## Kierunek wizualny

- neutralne premium tło dla wielu gatunków;
- bardzo ciemny granat zamiast czystej czerni;
- miętowy/turkus jako kolor funkcjonalnego CTA;
- fiolet jako akcent pomocniczy;
- kolory konkretnej gry ograniczone do jej prezentacji;
- delikatne cienie, hierarchia oparta głównie na kontraście, odstępach i obramowaniu.

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

## Testy Etapu 1

- `tests/Feature/ApplicationBootTest.php`
- `tests/Feature/DesignSystemAccessibilityTest.php`
- `tests/Feature/RolePermissionSeederTest.php`

Testy sprawdzają m.in. uruchomienie strony, routing obszarów, health endpoint, skip link, landmarks, relacje ARIA formularzy, modal, aria-live, reduced motion oraz minimalną szerokość layoutu.

## Zmienne `.env` wymagające decyzji

- `STUDIO_NAME`
- `PLATFORM_DOMAIN`
- `PAYMENT_PROVIDER`
- docelowe dane MariaDB dla produkcji
- docelowe SMTP

## Otwarte decyzje

1. Ostateczna nazwa studia.
2. Domena produkcyjna.
3. Operator płatności.
4. Docelowy object storage dla buildów.

Brak odpowiedzi na powyższe nie blokuje Etapu 2.

## Świadome odroczenia

- CMS i prawdziwa treść strony głównej: Etap 2;
- katalog/gatunki i realne dane kart gier: Etap 3;
- auth gracza/dewelopera: Etap 4;
- 2FA: Etap 4;
- pełne Policies rekordowe: wraz z modułami, zanim pojawią się dane wrażliwe;
- płatności i checkout: Etap 5;
- finalna konfiguracja SEOHost: Etap 11.

## Następny etap

**Etap 2 — CMS, strona główna i treści.**
