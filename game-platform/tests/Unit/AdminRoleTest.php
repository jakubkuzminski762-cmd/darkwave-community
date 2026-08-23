<?php

declare(strict_types=1);

namespace Tests\Unit;

use App\Enums\AdminRole;
use PHPUnit\Framework\TestCase;

class AdminRoleTest extends TestCase
{
    public function test_admin_roles_have_unique_slugs(): void
    {
        $slugs = array_map(
            fn (AdminRole $role): string => $role->value,
            AdminRole::cases(),
        );

        $this->assertSame($slugs, array_values(array_unique($slugs)));
    }
}
