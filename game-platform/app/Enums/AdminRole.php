<?php

declare(strict_types=1);

namespace App\Enums;

enum AdminRole: string
{
    case SuperAdmin = 'super-admin';
    case Administrator = 'administrator';
    case Editor = 'editor';
    case PublisherManager = 'publisher-manager';
    case SupportAgent = 'support-agent';
    case Finance = 'finance';
    case Analyst = 'analyst';

    public function label(): string
    {
        return match ($this) {
            self::SuperAdmin => 'Super admin',
            self::Administrator => 'Administrator',
            self::Editor => 'Redaktor',
            self::PublisherManager => 'Publisher manager',
            self::SupportAgent => 'Support agent',
            self::Finance => 'Finance',
            self::Analyst => 'Analyst',
        };
    }
}
