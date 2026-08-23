# PROJECT MANIFEST

## Projekt
Profesjonalna strona studia gier + platforma wydawniczo-sprzedażowa.

## Status
**Etap 0 — ukończony**  
**Etap 1 — ukończony**  
**Etap 2 — ukończony**  
**Etap 3 — ukończony w kodzie**

Publiczny staging: `https://darkwave-community-production.up.railway.app`

## Stack
Laravel 13 · PHP 8.3+ · MariaDB/MySQL · Blade · Tailwind CSS 4 · Alpine.js/Vite + statyczne assety stagingowe · database queue.

## Etap 3 — zakres zrealizowany
- modele i migracje katalogu: `games`, `genres`, `tags`, `platforms`, `game_modes`, `game_features`, media gier, trailery, wydania, premiery, release notes i wymagania systemowe;
- jeden gatunek główny + dodatkowe gatunki i tagi;
- słowniki platform, trybów gry i funkcji dostępności;
- statusy premiery: draft, announced, coming soon, early access, released, discontinued;
- osobny status publikacji chroniący drafty i treści przyszłe;
- publiczny katalog `/gry`;
- wyszukiwanie po tytule, opisie, studiu, wydawcy i tagach;
- filtry gatunku, platformy, trybu, języka, dostępności, statusu i przedziału cenowego;
- sortowanie polecane / najnowsze / data premiery / cena / alfabet;
- stan filtrów zapisany w URL, aktywne filtry, czyszczenie i server-side pagination;
- widok siatki i listy;
- strona gry `/gry/{slug}` z hero, CTA, ceną katalogową, platformami, cechami, gatunkami, tagami, trybami i dostępnością;
- galeria z lightboxem i opcjonalny trailer bez autoplay;
- wydania, wersje, wymagania minimalne/zalecane, press kit, wsparcie i aktualności gry;
- rekomendacje oparte na gatunkach i tagach, bez profilowania użytkownika;
- JSON-LD `VideoGame`;
- panel `/admin/games` chroniony `auth` + `games.manage`, z edycją relacji, trailera, wymagań i danych wydania;
- panel słowników `/admin/games/dictionaries` do dodawania gatunków, tagów, platform, trybów i deklaracji dostępności;
- seedery 8 demonstracyjnych gier i słowników;
- fabryki `Game`, `Genre`, `Tag`;
- testy filtrów, wyszukiwania, slugów, publikacji i uprawnień Redaktor vs Finance;
- homepage Etapu 2 podłączona do prawdziwych rekordów katalogu.

## Ważna granica Etapu 3
Cena w `game_editions` jest informacją katalogową potrzebną do kart i strony gry. Prawdziwe produkty, `product_prices`, koszyk, checkout, płatności i licencje pozostają do Etapu 5. Wishlist pozostaje do Etapu 4.

## Migracje
1. `0001_01_01_000000_create_users_table.php`
2. `0001_01_01_000001_create_cache_table.php`
3. `0001_01_01_000002_create_jobs_table.php`
4. `2026_08_23_000100_create_roles_and_permissions_tables.php`
5. `2026_08_23_000200_create_cms_tables.php`
6. `2026_08_23_000300_create_catalog_tables.php`

## Następny etap
**Etap 4 — konta i biblioteka gracza:** rejestracja, e-mail verification, logowanie, reset hasła, profile, sesje, preferencje, 2FA tam gdzie przewidziane, wishlist i pusty dashboard biblioteki.
