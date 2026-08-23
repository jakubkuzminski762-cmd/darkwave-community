<?php

declare(strict_types=1);
namespace App\Services\Catalog;
use App\Models\Game; use App\Models\GameEdition; use Illuminate\Database\Eloquent\Builder; use Illuminate\Http\Request;
class CatalogQueryService
{
    public function query(Request $request): Builder
    {
        $q=trim($request->string('q')->toString()); $genre=$request->string('genre')->toString(); $platform=$request->string('platform')->toString(); $mode=$request->string('mode')->toString(); $status=$request->string('status')->toString(); $accessibility=$request->string('accessibility')->toString(); $language=$request->string('language')->toString(); $minPrice=$request->integer('min_price'); $maxPrice=$request->integer('max_price'); $sort=$request->string('sort','featured')->toString();
        $query=Game::query()->publiclyVisible()->with(['genres','platforms','editions','primaryGenre']);
        if($q!==''){$query->where(function(Builder $b)use($q):void{$like='%'.$q.'%';$b->where('title','like',$like)->orWhere('short_description','like',$like)->orWhere('description','like',$like)->orWhere('developer_name','like',$like)->orWhere('publisher_name','like',$like)->orWhereHas('tags',fn(Builder $t)=>$t->where('name','like',$like));});}
        if($genre!=='')$query->whereHas('genres',fn(Builder $b)=>$b->where('genres.slug',$genre));
        if($platform!=='')$query->whereHas('platforms',fn(Builder $b)=>$b->where('platforms.slug',$platform));
        if($mode!=='')$query->whereHas('modes',fn(Builder $b)=>$b->where('game_modes.slug',$mode));
        if($status!=='')$query->where('release_status',$status); if($accessibility!=='')$query->whereHas('accessibilityFeatures',fn(Builder $b)=>$b->where('accessibility_features.slug',$accessibility)); if($language!=='')$query->whereJsonContains('languages',$language); if($minPrice>0)$query->whereHas('editions',fn(Builder $e)=>$e->where('price_minor','>=',$minPrice*100)); if($maxPrice>0)$query->whereHas('editions',fn(Builder $e)=>$e->where('price_minor','<=',$maxPrice*100));
        return match($sort){
            'newest'=>$query->orderByDesc('published_at'),
            'release_date'=>$query->orderByRaw('release_date IS NULL')->orderBy('release_date'),
            'price_asc'=>$this->orderByPrice($query,'asc'),
            'price_desc'=>$this->orderByPrice($query,'desc'),
            'title'=>$query->orderBy('title'),
            default=>$query->orderByRaw('featured_rank IS NULL')->orderBy('featured_rank')->orderByDesc('published_at'),
        };
    }
    private function orderByPrice(Builder $query,string $direction): Builder
    {
        $sub=GameEdition::query()->select('price_minor')->whereColumn('game_editions.game_id','games.id')->whereNotNull('price_minor')->orderBy('price_minor')->limit(1);
        return $query->orderBy($sub,$direction)->orderBy('title');
    }
}
