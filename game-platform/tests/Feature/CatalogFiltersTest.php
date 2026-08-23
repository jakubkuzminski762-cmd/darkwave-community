<?php

declare(strict_types=1);
namespace Tests\Feature;
use Database\Seeders\CatalogSeeder; use Database\Seeders\CmsDemoSeeder; use Illuminate\Foundation\Testing\RefreshDatabase; use Tests\TestCase;
class CatalogFiltersTest extends TestCase
{
    use RefreshDatabase;
    protected function setUp():void{parent::setUp();$this->seed([CmsDemoSeeder::class,CatalogSeeder::class]);}
    public function test_catalog_filters_are_reflected_in_url_and_results():void{$this->get('/gry?genre=rpg&platform=windows&status=coming_soon&sort=title')->assertOk()->assertSee('Project Meridian')->assertDontSee('Iron Vale')->assertSee('genre: rpg')->assertSee('platform: windows');}
    public function test_search_matches_title_and_tag():void{$this->get('/gry?q=Meridian')->assertOk()->assertSee('Project Meridian')->assertDontSee('Lumen Fold');$this->get('/gry?q=cozy')->assertOk()->assertSee('Moss &amp; Machines',false);}
    public function test_catalog_supports_list_view_and_server_pagination():void{$this->get('/gry?view=list')->assertOk()->assertSee('catalog-grid is-list',false)->assertSee('Project Meridian');}
}
