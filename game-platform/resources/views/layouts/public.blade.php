<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#080B12">
    <meta name="color-scheme" content="dark">
    <title>@yield('title', config('app.name'))</title>
    <link rel="stylesheet" href="/design-system.css?v=etap1-4">
    <script src="/design-system.js?v=etap1-4" defer></script>
</head>
<body>
    <a class="skip-link" href="#main-content">Przejdź do treści</a>

    <header class="site-header">
        <div class="container header-row">
            <a class="brand" href="{{ url('/') }}" aria-label="{{ config('app.name') }} — strona główna">
                <span class="brand-mark" aria-hidden="true">GS</span>
                <span class="brand-name">{{ config('app.name') }}</span>
            </a>

            <nav class="desktop-nav" aria-label="Główna nawigacja">
                <a class="nav-link" href="#games">Gry</a>
                <a class="nav-link" href="#components">Design system</a>
                <a class="nav-link" href="{{ route('developer.home') }}">Dla deweloperów</a>
                <a class="nav-link" href="{{ route('admin.home') }}">Panel</a>
            </nav>

            <div class="header-actions">
                <button class="icon-btn desktop-only" type="button" aria-label="Zmień język">PL</button>
                <a class="btn btn-primary" href="#games">Odkryj gry</a>
                <button class="icon-btn mobile-trigger" type="button" data-mobile-open aria-expanded="false" aria-controls="mobile-navigation" aria-label="Otwórz menu">
                    <span aria-hidden="true">☰</span>
                </button>
            </div>
        </div>

        <div id="mobile-navigation" class="mobile-menu" data-mobile-menu>
            <div class="container mobile-menu-inner">
                <div style="display:flex;justify-content:flex-end">
                    <button class="icon-btn" type="button" data-mobile-close aria-label="Zamknij menu">×</button>
                </div>
                <nav aria-label="Nawigacja mobilna" style="display:grid;gap:.25rem">
                    <a class="nav-link" href="#games" data-mobile-link>Gry</a>
                    <a class="nav-link" href="#components" data-mobile-link>Design system</a>
                    <a class="nav-link" href="{{ route('developer.home') }}">Dla deweloperów</a>
                    <a class="nav-link" href="{{ route('admin.home') }}">Panel administracyjny</a>
                </nav>
            </div>
        </div>
    </header>

    <main id="main-content" tabindex="-1">
        @yield('content')
    </main>

    <footer class="site-footer">
        <div class="container footer-grid">
            <div>
                <a class="brand" href="{{ url('/') }}">
                    <span class="brand-mark" aria-hidden="true">GS</span>
                    <span>{{ config('app.name') }}</span>
                </a>
                <p class="muted" style="max-width:34ch;margin:.9rem 0 0">Neutralna, premium platforma dla różnych gatunków gier. Etap 1 przygotowuje wspólny język interfejsu.</p>
            </div>
            <div>
                <p class="footer-title">Platforma</p>
                <div class="footer-links">
                    <a href="#games">Gry</a>
                    <a href="#components">Komponenty</a>
                    <a href="{{ route('api.v1.status') }}">Status API</a>
                </div>
            </div>
            <div>
                <p class="footer-title">Deweloperzy</p>
                <div class="footer-links">
                    <a href="{{ route('developer.home') }}">Portal dewelopera</a>
                    <a href="#forms">Formularze</a>
                    <a href="#states">Stany UI</a>
                </div>
            </div>
            <div>
                <p class="footer-title">Informacje</p>
                <div class="footer-links">
                    <a href="#accessibility">Dostępność</a>
                    <a href="#tokens">Tokeny</a>
                    <a href="{{ route('admin.home') }}">Administracja</a>
                </div>
            </div>
        </div>
        <div class="container footer-bottom">Etap 1 · Design system i layout · Bez logiki sklepu</div>
    </footer>

    @stack('overlays')
</body>
</html>
