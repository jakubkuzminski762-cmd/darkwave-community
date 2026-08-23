<?php

declare(strict_types=1);
namespace App\Http\Controllers\Catalog;
use App\Enums\ContentStatus; use App\Enums\GameReleaseStatus; use App\Http\Controllers\Controller; use App\Http\Requests\Catalog\SaveGameRequest; use App\Models\AccessibilityFeature; use App\Models\Game; use App\Models\GameMode; use App\Models\Genre; use App\Models\Media; use App\Models\Platform; use App\Models\Tag; use Illuminate\Http\RedirectResponse; use Illuminate\Support\Arr; use Illuminate\View\View;
class AdminGameController extends Controller
{
    public function index(): View{return view('admin.catalog.index',['games'=>Game::query()->with(['genres','platforms'])->orderByDesc('updated_at')->paginate(25)]);}
    public function create(): View{return $this->form(new Game());}
    public function store(SaveGameRequest $request): RedirectResponse{$game=Game::query()->create($this->baseData($request));$this->sync($game,$request);return redirect()->route('admin.games.edit',$game)->with('status','Gra została utworzona.');}
    public function edit(Game $game): View{$game->load(['genres','tags','platforms','modes','accessibilityFeatures','editions','trailers','media.media','releases','systemRequirements']);return $this->form($game);}
    public function update(SaveGameRequest $request,Game $game): RedirectResponse{$game->update($this->baseData($request));$this->sync($game,$request);return back()->with('status','Gra została zapisana.');}
    private function form(Game $game): View{return view('admin.catalog.form',['game'=>$game,'genres'=>Genre::query()->orderBy('sort_order')->get(),'tags'=>Tag::query()->orderBy('name')->get(),'platforms'=>Platform::query()->orderBy('sort_order')->get(),'modes'=>GameMode::query()->orderBy('sort_order')->get(),'accessibility'=>AccessibilityFeature::query()->orderBy('name')->get(),'releaseStatuses'=>GameReleaseStatus::cases(),'publicationStatuses'=>ContentStatus::cases()]);}
    private function baseData(SaveGameRequest $request): array{$data=Arr::only($request->validated(),['title','slug','short_description','description','developer_name','publisher_name','publication_status','published_at','release_status','release_date','featured_rank','age_rating','cover_url','hero_url','accent','website_url','press_kit_url','support_url','seo_title','seo_description']);$data['languages']=collect(explode(',',(string)$request->validated('languages_text','')))->map(fn($v)=>trim($v))->filter()->values()->all();return $data;}
    private function sync(Game $game,SaveGameRequest $request): void
    {
        $data=$request->validated();
        $genres=collect($data['genre_ids']??[])->unique()->mapWithKeys(fn($id)=>[(int)$id=>['is_primary'=>(int)$id===(int)($data['primary_genre_id']??0)]])->all(); if(($data['primary_genre_id']??null)&&!array_key_exists((int)$data['primary_genre_id'],$genres))$genres[(int)$data['primary_genre_id']]=['is_primary'=>true];
        $game->genres()->sync($genres); $game->tags()->sync($data['tag_ids']??[]); $game->platforms()->sync($data['platform_ids']??[]); $game->modes()->sync($data['mode_ids']??[]); $game->accessibilityFeatures()->sync($data['accessibility_ids']??[]);
        $edition=$game->editions()->firstOrCreate(['slug'=>'standard'],['name'=>'Standard','is_default'=>true]); $edition->update(['price_minor'=>$data['price_minor']??null,'currency'=>strtoupper($data['currency']??'PLN')]);
        $platformId=collect($data['platform_ids']??[])->first(); if($platformId){$game->releases()->updateOrCreate(['game_edition_id'=>$edition->id,'platform_id'=>$platformId],['status'=>$data['release_status'],'version'=>$data['release_version']??null,'release_date'=>$data['release_date']??null,'file_size_bytes'=>isset($data['release_file_size_gb'])?(int)round((float)$data['release_file_size_gb']*1000000000):null]); foreach(['minimum','recommended'] as $type){$prefix=$type.'_'; if(collect(['os','cpu','memory','gpu','storage'])->contains(fn($field)=>!empty($data[$prefix.$field]??null)))$game->systemRequirements()->updateOrCreate(['platform_id'=>$platformId,'type'=>$type],['os'=>$data[$prefix.'os']??null,'cpu'=>$data[$prefix.'cpu']??null,'memory'=>$data[$prefix.'memory']??null,'gpu'=>$data[$prefix.'gpu']??null,'storage'=>$data[$prefix.'storage']??null]);}}
        if(!empty($data['trailer_embed_url']))$game->trailers()->updateOrCreate(['is_primary'=>true],['title'=>'Trailer','provider'=>'embed','embed_url'=>$data['trailer_embed_url'],'position'=>1]);
        if(trim((string)($data['screenshot_urls']??''))!==''){$game->media()->delete(); foreach(preg_split('/\r?\n/',trim($data['screenshot_urls'])) as $i=>$url){$url=trim($url);if($url==='')continue;$media=Media::query()->create(['name'=>$game->title.' — screenshot '.($i+1),'disk'=>'external','url'=>$url,'alt_text'=>'Screenshot gry '.$game->title,'mime_type'=>'image/external']);$game->media()->create(['media_id'=>$media->id,'type'=>'screenshot','position'=>$i]);}}
    }
}
