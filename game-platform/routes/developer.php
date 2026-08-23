<?php

declare(strict_types=1);

use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Developer portal routes
|--------------------------------------------------------------------------
|
| Etap 0 udostępnia wyłącznie ekran techniczny fundamentu. Logowanie,
| izolacja studiów i Policies pojawią się przed właściwymi funkcjami portalu.
|
*/

Route::view('/', 'foundation.developer')->name('home');
