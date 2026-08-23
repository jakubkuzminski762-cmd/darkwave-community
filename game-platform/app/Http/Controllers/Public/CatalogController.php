<?php

declare(strict_types=1);
namespace App\Http\Controllers\Public;
use App\Enums\GameReleaseStatus; use App\Models\AccessibilityFeature; use App\Http\Controllers\Controller; use App\Models\GameMode; use App\Models\Genre; use App\Models\Platform; use App\Services\Catalog\CatalogQueryService; use App\Services\Cms\PublicContentService; use Illuminate\Http\Request; use Illuminate\View\View;
class CatalogController extends Controller
{
    public function __invoke(Request $request,CatalogQueryService $catalog,PublicContentService $content): View
    {
        return view('public.games.index',['games'=>$catalog->query($request)->paginate(12)->withQueryString(),'genres'=>Genre::query()->orderBy('sort_order')->orderBy('name')->get(),'platforms'=>Platform::query()->orderBy('sort_order')->get(),'modes'=>GameMode::query()->orderBy('sort_order')->get(),'accessibility'=>AccessibilityFeature::query()->orderBy('name')->get(),'languages'=>['Polski','English','Deutsch','Français','Español'],'statuses'=>GameReleaseStatus::cases(),'menus'=>$content->menus(),'viewMode'=>in_array($request->string('view')->toString(),['grid','list'],true)?$request->string('view')->toString():'grid']);
    }
}
