# Game Studio Platform — Etap 0

Fundament profesjonalnej strony studia gier i platformy wydawniczo-sprzedażowej.

## Stack

- Laravel 13
- PHP 8.3+
- MariaDB (sterownik `mysql`)
- Blade
- Tailwind CSS 4
- Alpine.js
- Vite
- Database queue

## Wymagania lokalne

- PHP 8.3 lub nowszy
- Composer 2
- Node.js 20+ / npm
- MariaDB
- rozszerzenia PHP wymagane przez Laravel, w tym PDO MySQL

## Instalacja

```bash
cp .env.example .env
composer install
php artisan key:generate
```

Utwórz bazę MariaDB i użytkownika zgodnie z wartościami w `.env`, a następnie:

```bash
php artisan migrate --seed
npm install
npm run build
php artisan test
php artisan serve
```

Aplikacja będzie dostępna pod `http://localhost:8000`.

## Tryb developerski

Po pierwszej instalacji możesz uruchomić serwer, kolejkę, logi i Vite jednym poleceniem:

```bash
composer run dev
```

## Routing obszarów

- `/` — serwis publiczny
- `/admin` — panel administracyjny (na Etapie 0 wyłącznie bezpieczny ekran techniczny)
- `/developer` — portal dewelopera (na Etapie 0 wyłącznie bezpieczny ekran techniczny)
- `/api/v1/status` — status API
- `/up` — health check Laravel

## MariaDB

Konfiguracja startowa:

```dotenv
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=game_studio_platform
DB_USERNAME=game_studio_platform
DB_PASSWORD=
```

Testy automatyczne używają SQLite `:memory:` wyłącznie dla szybkości. Środowiska staging/production pozostają na MariaDB.

## Kolejka i hosting współdzielony

Domyślnie używana jest kolejka bazodanowa:

```dotenv
QUEUE_CONNECTION=database
```

W `routes/console.php` przewidziano krótki `queue:work --stop-when-empty` uruchamiany przez scheduler w środowiskach `staging` i `production`. Na SEOHost wystarczy później jeden CRON do `php artisan schedule:run`; nie zakładamy Supervisora ani roota.

## Bezpieczeństwo fundamentu

- `.env` nie jest wersjonowany.
- prywatny disk wskazuje na `storage/app/private`;
- role i permissions są rozdzielone i seedowane idempotentnie;
- `Super admin` otrzymuje pełny zakres;
- pozostałe role otrzymują zakres wynikający ze specyfikacji;
- właściwa autoryzacja rekordów (Policies) będzie dodawana przed implementacją wrażliwych operacji;
- panel i portal na Etapie 0 nie udostępniają danych ani operacji biznesowych.

## Testy Etapu 0

```bash
php artisan test
```

Testy sprawdzają:

1. uruchomienie aplikacji publicznej;
2. endpoint `/up`;
3. routing public/admin/developer/API;
4. idempotencję seedera ról i uprawnień;
5. unikalność enumów ról.

## Następny etap

**Etap 1 — design system i layout**.
