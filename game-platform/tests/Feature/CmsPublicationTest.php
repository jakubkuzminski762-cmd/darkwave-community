<?php

declare(strict_types=1);

namespace Tests\Feature;

use App\Enums\ContentStatus;
use App\Models\Post;
use App\Models\PostCategory;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class CmsPublicationTest extends TestCase
{
    use RefreshDatabase;

    public function test_future_scheduled_post_is_not_public_but_past_scheduled_post_is(): void
    {
        $category=PostCategory::query()->create(['name'=>'Studio','slug'=>'studio']);
        Post::query()->create(['post_category_id'=>$category->id,'title'=>'Future','slug'=>'future','body'=>'x','status'=>ContentStatus::Scheduled,'published_at'=>now()->addDay()]);
        Post::query()->create(['post_category_id'=>$category->id,'title'=>'Ready','slug'=>'ready','body'=>'x','status'=>ContentStatus::Scheduled,'published_at'=>now()->subMinute()]);
        $this->get('/aktualnosci/future')->assertNotFound();
        $this->get('/aktualnosci/ready')->assertOk()->assertSee('Ready');
    }

    public function test_draft_post_is_not_public(): void
    {
        Post::query()->create(['title'=>'Draft','slug'=>'draft','body'=>'x','status'=>ContentStatus::Draft]);
        $this->get('/aktualnosci/draft')->assertNotFound();
    }
}
