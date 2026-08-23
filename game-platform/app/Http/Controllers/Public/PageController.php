<?php

declare(strict_types=1);

namespace App\Http\Controllers\Public;

use App\Http\Controllers\Controller;
use App\Models\Page;
use App\Services\Cms\PublicContentService;
use Illuminate\View\View;

class PageController extends Controller
{
    public function __invoke(string $slug, PublicContentService $content): View
    {
        $page = Page::query()->publiclyVisible()->where('slug',$slug)->with(['blocks'=>fn($q)=>$q->where('is_enabled',true)])->firstOrFail();
        return view('public.page',['page'=>$page,'menus'=>$content->menus(),'isPreview'=>false]);
    }
}
