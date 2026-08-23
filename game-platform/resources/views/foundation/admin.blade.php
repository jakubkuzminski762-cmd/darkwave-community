<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Panel administracyjny — {{ config('app.name') }}</title>
    @vite(['resources/css/app.css', 'resources/js/app.js'])
</head>
<body>
    <main class="foundation">
        <p class="eyebrow">Admin / Etap 0</p>
        <h1>Panel administracyjny</h1>
        <p>Routing panelu jest gotowy. Ten ekran nie zawiera danych wrażliwych ani operacji administracyjnych.</p>
        <a href="{{ route('home') }}">Wróć na stronę główną</a>
    </main>
</body>
</html>
