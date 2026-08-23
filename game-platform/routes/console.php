<?php

declare(strict_types=1);

use Illuminate\Support\Facades\Artisan;
use Illuminate\Support\Facades\Schedule;

Artisan::command('platform:about', function (): void {
    $this->info('Game Studio Platform — fundament Laravel 13 gotowy.');
})->purpose('Wyświetla status fundamentu platformy.');

Schedule::command('queue:work --stop-when-empty --tries=3 --timeout=60')
    ->everyMinute()
    ->withoutOverlapping()
    ->environments(['staging', 'production']);
