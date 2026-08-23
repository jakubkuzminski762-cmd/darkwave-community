# PROJECT MANIFEST

## Projekt
Profesjonalna strona studia gier + platforma wydawniczo-sprzedażowa.

## Status
**Etap 0 — ukończony**  
**Etap 1 — ukończony**  
**Etap 2 — ukończony w kodzie**

## Stack
Laravel 13 · PHP 8.3+ · MariaDB/MySQL · Blade · Tailwind CSS 4 · Alpine.js · Vite · database queue.

## Etap 2 — zakres zrealizowany
- publiczna homepage oparta na danych CMS;
- `pages` i `page_blocks` dla stron i bloków treści;
- dynamiczne menu główne i stopka prawna (`menus`);
- aktualności (`posts`) i pięć kategorii przewidzianych w specyfikacji;
- statusy `draft`, `scheduled`, `published`;
- publikacja planowana bez ujawniania treści przed czasem;
- podpisane, czasowe URL-e podglądu stron i postów;
- biblioteka metadanych mediów (`media`) bez przechowywania dużych plików na hostingu;
- SEO title, description, canonical i podstawowe Open Graph;
- `noindex,nofollow` dla podglądów CMS;
- podstawowy panel CMS chroniony `auth` + Gates (`cms.manage`, `news.manage`);
- edycja stron i aktualności;
- seeder przykładowej homepage, stron, menu, mediów i aktualności;
- pełne stany publiczne dla braku treści;
- animacje progresywne i `prefers-reduced-motion`;
- test widoczności publikacji planowanej i wersji roboczych;
- test uprawnień Redaktor vs Finance;
- zaktualizowany smoke test aplikacji.

## Dane demonstracyjne
Homepage: Project Meridian + 6 kart demonstracyjnych gatunków, premiery, 4-etapowy skrót procesu wydawniczego, aktualności i społeczność.  
Strony: `/wydaj-z-nami`, `/o-nas`, `/polityka-prywatnosci`.  
Aktualności: `/aktualnosci` i przykładowe opublikowane wpisy.  
Design system z Etapu 1 pozostaje pod `/design-system`.

## Bezpieczeństwo / świadome decyzje
- Seeder nie tworzy żadnego konta ani przewidywalnego hasła.
- Panel CMS wymaga zalogowanego użytkownika i uprawnień; właściwe logowanie UI powstanie w Etapie 4.
- Podgląd treści jest dostępny wyłącznie przez czasowy podpisany URL.
- Newsletter ma gotowy stan UI, ale zapis adresów nie jest jeszcze uruchomiony; moduł komunikacji/e-mail jest w późniejszym etapie.
- Rzeczywiste rekordy gier i ich relacje nie są tworzone w Etapie 2; homepage używa danych bloków CMS do czasu Etapu 3.

## Migracje
1. `0001_01_01_000000_create_users_table.php`
2. `0001_01_01_000001_create_cache_table.php`
3. `0001_01_01_000002_create_jobs_table.php`
4. `2026_08_23_000100_create_roles_and_permissions_tables.php`
5. `2026_08_23_000200_create_cms_tables.php`

## Następny etap
**Etap 3 — katalog wielu gatunków:** gry, gatunki, tagi, platformy, tryby, media gier, wymagania, wydania, filtry URL, sortowanie, pagination i pełna strona gry.
