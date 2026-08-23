# Game Platform — isolated workspace

Ten katalog jest przeznaczony wyłącznie dla nowej platformy studia gier.

## Zasady izolacji

- istniejąca strona/aplikacja w głównym katalogu repozytorium pozostaje bez zmian;
- nowa platforma będzie rozwijana pod `game-platform/`;
- prace nad platformą prowadzone są na osobnym branchu `game-platform`;
- wdrożenie podglądowe powinno używać brancha `game-platform` i root directory `game-platform`;
- nie należy wskazywać głównego katalogu repozytorium jako katalogu tej aplikacji;
- produkcyjny branch `main` obecnej aplikacji nie jest modyfikowany przez prace nad platformą.

Docelowy stos platformy: Laravel 13, PHP 8.3+, MariaDB, Blade, Tailwind CSS i Alpine.js.
