<?php

declare(strict_types=1);

use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Admin routes
|--------------------------------------------------------------------------
|
| Etap 0 rejestruje wyłącznie bezpieczny ekran fundamentu. Autoryzacja
| panelu administracyjnego zostanie dołączona przed dodaniem danych
| wrażliwych i operacji biznesowych.
|
*/

Route::view('/', 'foundation.admin')->name('home');
