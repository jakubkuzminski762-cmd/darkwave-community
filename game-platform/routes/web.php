<?php

declare(strict_types=1);
use App\Http\Controllers\Public\CatalogController; use App\Http\Controllers\Public\GameController; use App\Http\Controllers\Public\HomeController; use App\Http\Controllers\Public\NewsController; use App\Http\Controllers\Public\PageController; use App\Http\Controllers\Public\PreviewController; use Illuminate\Support\Facades\Route;
Route::get('/',HomeController::class)->name('home'); Route::view('/design-system','foundation.home')->name('design-system');
Route::get('/gry',CatalogController::class)->name('games.index'); Route::get('/gry/{slug}',GameController::class)->name('games.show');
Route::get('/aktualnosci',[NewsController::class,'index'])->name('news.index'); Route::get('/aktualnosci/{slug}',[NewsController::class,'show'])->name('news.show');
Route::get('/preview/page/{page}',[PreviewController::class,'page'])->middleware('signed')->name('preview.page'); Route::get('/preview/post/{post}',[PreviewController::class,'post'])->middleware('signed')->name('preview.post');
Route::get('/{slug}',PageController::class)->where('slug','[a-z0-9-]+')->name('pages.show');
