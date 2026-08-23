<?php

declare(strict_types=1);

namespace App\Enums;

enum ContentStatus: string
{
    case Draft = 'draft';
    case Scheduled = 'scheduled';
    case Published = 'published';

    public function label(): string
    {
        return match ($this) {
            self::Draft => 'Wersja robocza',
            self::Scheduled => 'Zaplanowano',
            self::Published => 'Opublikowano',
        };
    }
}
