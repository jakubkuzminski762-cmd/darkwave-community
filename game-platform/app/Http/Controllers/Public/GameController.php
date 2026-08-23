<?php

declare(strict_types=1);
namespace App\Http\Controllers\Public;
use App\Http\Controllers\Controller; use App\Models\Game; use App\Services\Cms\PublicContentService; use Illuminate\View\View;
class GameController extends Controller
{
    public function __invoke(string $slug,PublicContentService $content): View
    {
        $game=Game::query()->publiclyVisible()->where('slug',$slug)->with(['genres','tags','platforms','modes','accessibilityFeatures','features','media.media','trailers','editions','releases.platform','releases.edition','releases.notes','systemRequirements.platform','posts'=>fn($q)=>$q->publiclyVisible()->orderByDesc('published_at')->limit(3)])->firstOrFail();
        $genreIds=$game->genres->pluck('id'); $tagIds=$game->tags->pluck('id');
        $recommendations=Game::query()->publiclyVisible()->whereKeyNot($game->id)->where(function($q)use($genreIds,$tagIds){$q->whereHas('genres',fn($g)=>$g->whereIn('genres.id',$genreIds))->orWhereHas('tags',fn($t)=>$t->whereIn('tags.id',$tagIds));})->with(['genres','platforms','editions'])->limit(4)->get();
        return view('public.games.show',['game'=>$game,'recommendations'=>$recommendations,'menus'=>$content->menus()]);
    }
}
