<?php

declare(strict_types=1);

namespace Tests\Feature;

use App\Enums\AdminRole;
use App\Models\Role;
use App\Models\User;
use Database\Seeders\RolePermissionSeeder;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class EditorCmsPermissionTest extends TestCase
{
    use RefreshDatabase;

    public function test_editor_can_open_cms_and_finance_cannot(): void
    {
        $this->seed(RolePermissionSeeder::class);
        $editor=User::factory()->create(); $editor->roles()->attach(Role::query()->where('slug',AdminRole::Editor->value)->firstOrFail());
        $finance=User::factory()->create(); $finance->roles()->attach(Role::query()->where('slug',AdminRole::Finance->value)->firstOrFail());
        $this->actingAs($editor)->get('/admin/cms')->assertOk();
        $this->actingAs($finance)->get('/admin/cms')->assertForbidden();
    }
}
