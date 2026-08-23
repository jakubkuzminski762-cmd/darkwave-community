<?php

declare(strict_types=1);
namespace Tests\Feature;
use App\Enums\AdminRole; use App\Models\Role; use App\Models\User; use Database\Seeders\RolePermissionSeeder; use Illuminate\Foundation\Testing\RefreshDatabase; use Tests\TestCase;
class AdminCatalogPermissionTest extends TestCase
{
    use RefreshDatabase;
    protected function setUp():void{parent::setUp();$this->seed(RolePermissionSeeder::class);}
    private function userWithRole(AdminRole $role):User{$user=User::factory()->create();$user->roles()->attach(Role::query()->where('slug',$role->value)->firstOrFail());return $user;}
    public function test_editor_can_manage_games():void{$this->actingAs($this->userWithRole(AdminRole::Editor))->get('/admin/games')->assertOk()->assertSee('Gry');}
    public function test_finance_cannot_manage_games():void{$this->actingAs($this->userWithRole(AdminRole::Finance))->get('/admin/games')->assertForbidden();}
}
