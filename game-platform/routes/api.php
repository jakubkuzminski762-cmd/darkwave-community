<?php

declare(strict_types=1);

use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Route;

Route::get('/status', fn (): JsonResponse => response()->json([
    'status' => 'ok',
    'api' => 'v1',
]))->name('status');
