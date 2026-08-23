<?php

declare(strict_types=1);

namespace Tests\Feature;

use Tests\TestCase;

class DesignSystemAccessibilityTest extends TestCase
{
    public function test_public_layout_contains_keyboard_and_landmark_support(): void
    {
        $this->get('/')
            ->assertOk()
            ->assertSee('Przejdź do treści')
            ->assertSee('id="main-content"', false)
            ->assertSee('aria-label="Główna nawigacja"', false)
            ->assertSee('aria-controls="mobile-navigation"', false)
            ->assertSee('aria-modal="true"', false)
            ->assertSee('aria-live="polite"', false)
            ->assertSee('aria-invalid="true"', false)
            ->assertSee('design-system.css', false)
            ->assertSee('design-system.js', false);
    }

    public function test_design_system_css_respects_reduced_motion_and_mobile_width(): void
    {
        $css = file_get_contents(public_path('design-system.css'));

        self::assertIsString($css);
        self::assertStringContainsString('@media(prefers-reduced-motion:reduce)', $css);
        self::assertStringContainsString('min-width:320px', $css);
        self::assertStringContainsString('--content-max:1280px', $css);
        self::assertStringContainsString('--color-primary:#5ce1b9', $css);
    }

    public function test_form_demo_has_explicit_labels_and_error_relationships(): void
    {
        $this->get('/')
            ->assertOk()
            ->assertSee('for="demo-title"', false)
            ->assertSee('id="demo-title-help"', false)
            ->assertSee('aria-describedby="demo-error-message"', false);
    }
}
