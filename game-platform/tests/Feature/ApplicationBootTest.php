<?php

declare(strict_types=1);

namespace Tests\Feature;

use Tests\TestCase;

class ApplicationBootTest extends TestCase
{
    public function test_public_application_boots(): void
    {
        $this->get('/')
            ->assertOk()
            ->assertSee('Jedna marka.')
            ->assertSee('Design system');
    }

    public function test_health_endpoint_is_available(): void
    {
        $this->get('/up')->assertOk();
    }

    public function test_area_routes_are_registered(): void
    {
        $this->get('/admin')->assertOk()->assertSee('Panel administracyjny');
        $this->get('/developer')->assertOk()->assertSee('Portal dewelopera');

        $this->getJson('/api/v1/status')
            ->assertOk()
            ->assertJson([
                'status' => 'ok',
                'api' => 'v1',
            ]);
    }
}
