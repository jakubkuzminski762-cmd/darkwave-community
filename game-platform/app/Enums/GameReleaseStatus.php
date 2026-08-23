<?php

declare(strict_types=1);

namespace App\Enums;

enum GameReleaseStatus: string
{
    case Draft = 'draft'; case Announced = 'announced'; case ComingSoon = 'coming_soon'; case EarlyAccess = 'early_access'; case Released = 'released'; case Discontinued = 'discontinued';
    public function label(): string { return match($this){self::Draft=>'Szkic',self::Announced=>'Zapowiedziana',self::ComingSoon=>'Wkrótce',self::EarlyAccess=>'Early access',self::Released=>'Dostępna',self::Discontinued=>'Wycofana'}; }
}
