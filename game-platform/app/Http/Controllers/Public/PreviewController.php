<?php

declare(strict_types=1);

namespace App\Http\Controllers\Public;

use App\Http\Controllers\Controller;
use App\Models\Page;
use App\Models\Post;
use App\Services\Cms\PublicContentService;
use Illuminate\View\View;

class PreviewController extends Controller
{
    public function page(Page $page, PublicContentService $content): View { $page->load('blocks'); return view('public.page',['page'=>$page,'menus'=>$content->menus(),'isPreview'=>true]); }
    public function post(Post $post, PublicContentService $content): View { $post->load(['category','media']); return view('public.news.show',['post'=>$post,'menus'=>$content->menus(),'isPreview'=>true]); }
}
