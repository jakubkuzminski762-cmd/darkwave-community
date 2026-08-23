<?php

declare(strict_types=1);
namespace App\Services\Cms;
use App\Models\Game; use App\Models\Menu; use App\Models\Page; use App\Models\Post; use Illuminate\Support\Collection;
class PublicContentService
{
    public function menus():Collection{return Menu::query()->where('is_enabled',true)->get()->keyBy('location');}
    public function homepage():array{$page=Page::query()->publiclyVisible()->where('slug','home')->with(['blocks'=>fn($q)=>$q->where('is_enabled',true)])->firstOrFail();return['page'=>$page,'blocks'=>$page->blocks->keyBy('type'),'featuredGames'=>Game::query()->publiclyVisible()->with(['genres','platforms','editions','primaryGenre'])->orderByRaw('featured_rank IS NULL')->orderBy('featured_rank')->limit(6)->get(),'upcomingGames'=>Game::query()->publiclyVisible()->whereIn('release_status',['announced','coming_soon','early_access'])->orderByRaw('release_date IS NULL')->orderBy('release_date')->limit(4)->get(),'latestPosts'=>Post::query()->publiclyVisible()->with('category')->orderByDesc('published_at')->limit(3)->get(),'menus'=>$this->menus()];}
}
