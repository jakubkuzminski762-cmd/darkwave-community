<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Portal dewelopera — {{ config('app.name') }}</title>
    @vite(['resources/css/app.css', 'resources/js/app.js'])
</head>
<body>
    <main class="foundation">
        <p class="eyebrow">Developer / Etap 0</p>
        <h1>Portal dewelopera</h1>
        <p>Routing portalu jest gotowy. Funkcje kont, studiów i zgłoszeń powstaną w kolejnych etapach.</p>
        <a href="{{ route('home') }}">Wróć na stronę główną</a>
    </main>
</body>
</html>
