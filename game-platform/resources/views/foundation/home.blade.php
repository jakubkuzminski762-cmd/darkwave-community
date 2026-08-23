<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>{{ config('app.name') }}</title>
    @vite(['resources/css/app.css', 'resources/js/app.js'])
</head>
<body>
    <main class="foundation">
        <p class="eyebrow">Etap 0</p>
        <h1>{{ config('app.name') }}</h1>
        <p>Fundament aplikacji działa. Warstwa publiczna, panel administracyjny, portal dewelopera i API są rozdzielone routingiem.</p>
        <nav aria-label="Obszary fundamentu">
            <a href="{{ route('admin.home') }}">Panel administracyjny</a>
            <a href="{{ route('developer.home') }}">Portal dewelopera</a>
            <a href="{{ route('api.v1.status') }}">API v1 status</a>
        </nav>
    </main>
</body>
</html>
