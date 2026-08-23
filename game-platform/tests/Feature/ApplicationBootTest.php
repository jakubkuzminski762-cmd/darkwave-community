<?php

declare(strict_types=1);

namespace Tests\Feature;

use Database\Seeders\CmsDemoSeeder;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ApplicationBootTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();
        $this->seed(CmsDemoSeeder::class);
    }

    public function test_public_application_boots(): void
    {
        $this->get('/')->assertOk()->assertSee('Project Meridian')->assertSee('Znajdź swój następny świat');
    }

    public function test_health_endpoint_is_available(): void
    {
        $this->get('/up')->assertOk();
    }

    public function test_area_routes_are_registered(): void
    {
        $this->get('/admin')->assertOk()->assertSee('Panel administracyjny');
        $this->get('/developer')->assertOk()->assertSee('Portal dewelopera');
        $this->getJson('/api/v1/status')->assertOk()->assertJson(['status'=>'ok','api'=>'v1']);
    }
}
