<?php

declare(strict_types=1);

use App\Http\Controllers\Cms\CmsController;
use Illuminate\Support\Facades\Route;

Route::view('/','foundation.admin')->name('home');
Route::middleware(['auth','can:admin.access'])->group(function (): void {
    Route::get('/cms',[CmsController::class,'index'])->middleware('can:cms.manage')->name('cms.index');
    Route::get('/cms/pages/{page}/edit',[CmsController::class,'editPage'])->middleware('can:cms.manage')->name('cms.pages.edit');
    Route::put('/cms/pages/{page}',[CmsController::class,'updatePage'])->middleware('can:cms.manage')->name('cms.pages.update');
    Route::get('/cms/posts/{post}/edit',[CmsController::class,'editPost'])->middleware('can:news.manage')->name('cms.posts.edit');
    Route::put('/cms/posts/{post}',[CmsController::class,'updatePost'])->middleware('can:news.manage')->name('cms.posts.update');
});
