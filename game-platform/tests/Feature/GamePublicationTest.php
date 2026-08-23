<?php

declare(strict_types=1);
namespace Tests\Feature;
use App\Enums\ContentStatus; use App\Enums\GameReleaseStatus; use App\Models\Game; use Database\Seeders\CatalogSeeder; use Database\Seeders\CmsDemoSeeder; use Illuminate\Foundation\Testing\RefreshDatabase; use Tests\TestCase;
class GamePublicationTest extends TestCase
{
    use RefreshDatabase;
    protected function setUp():void{parent::setUp();$this->seed([CmsDemoSeeder::class,CatalogSeeder::class]);}
    public function test_friendly_slug_opens_published_game_with_video_game_schema():void{$this->get('/gry/project-meridian')->assertOk()->assertSee('Project Meridian')->assertSee('"@type":"VideoGame"',false);}
    public function test_draft_game_is_not_public():void{$game=Game::factory()->draft()->create(['slug'=>'tajny-projekt']);$this->get('/gry/'.$game->slug)->assertNotFound();}
    public function test_scheduled_publication_stays_hidden_until_date():void{$game=Game::factory()->create(['slug'=>'przyszla-gra','publication_status'=>ContentStatus::Scheduled,'published_at'=>now()->addDay(),'release_status'=>GameReleaseStatus::Announced]);$this->get('/gry/'.$game->slug)->assertNotFound();$game->update(['published_at'=>now()->subMinute()]);$this->get('/gry/'.$game->slug)->assertOk();}
}
