<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="robots" content="noindex">
    <title>@yield('title') — {{ config('app.name') }}</title>
    <style>
        :root { color-scheme: dark; font-family: system-ui, sans-serif; }
        body { margin: 0; min-height: 100vh; display: grid; place-items: center; background: #080b12; color: #f4f7fb; }
        main { width: min(42rem, calc(100% - 2rem)); }
        p { color: #a4aec2; line-height: 1.6; }
        a { color: #5ce1b9; }
        a:focus-visible { outline: 3px solid #7a6bff; outline-offset: 4px; }
    </style>
</head>
<body>
    <main>
        <p>@yield('code')</p>
        <h1>@yield('title')</h1>
        <p>@yield('message')</p>
        <a href="{{ route('home') }}">Wróć na stronę główną</a>
    </main>
</body>
</html>
