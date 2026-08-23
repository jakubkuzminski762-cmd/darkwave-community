<?php

declare(strict_types=1);

namespace Tests\Feature;

use App\Enums\AdminRole;
use App\Enums\Permission;
use App\Models\Role;
use Database\Seeders\RolePermissionSeeder;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class RolePermissionSeederTest extends TestCase
{
    use RefreshDatabase;

    public function test_role_and_permission_matrix_is_seeded_idempotently(): void
    {
        $this->seed(RolePermissionSeeder::class);
        $this->seed(RolePermissionSeeder::class);

        $this->assertDatabaseCount('roles', count(AdminRole::cases()));
        $this->assertDatabaseCount('permissions', count(Permission::cases()));

        $superAdmin = Role::query()
            ->where('slug', AdminRole::SuperAdmin->value)
            ->firstOrFail();

        $this->assertCount(count(Permission::cases()), $superAdmin->permissions);

        $editor = Role::query()
            ->where('slug', AdminRole::Editor->value)
            ->firstOrFail();

        $this->assertTrue(
            $editor->permissions->contains('slug', Permission::CmsManage->value)
        );

        $this->assertFalse(
            $editor->permissions->contains('slug', Permission::FinanceManage->value)
        );
    }
}
