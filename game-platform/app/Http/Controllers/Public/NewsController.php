<?php

declare(strict_types=1);
namespace App\Http\Controllers\Public;
use App\Http\Controllers\Controller; use App\Models\Post; use App\Models\PostCategory; use App\Services\Cms\PublicContentService; use Illuminate\Http\Request; use Illuminate\View\View;
class NewsController extends Controller
{
    public function index(Request $request,PublicContentService $content):View{$category=$request->string('category')->toString();$posts=Post::query()->publiclyVisible()->with(['category','media','game'])->when($category!=='',fn($q)=>$q->whereHas('category',fn($c)=>$c->where('slug',$category)))->orderByDesc('published_at')->paginate(9)->withQueryString();return view('public.news.index',['posts'=>$posts,'categories'=>PostCategory::query()->orderBy('sort_order')->get(),'activeCategory'=>$category,'menus'=>$content->menus()]);}
    public function show(string $slug,PublicContentService $content):View{$post=Post::query()->publiclyVisible()->with(['category','media','game'])->where('slug',$slug)->firstOrFail();return view('public.news.show',['post'=>$post,'menus'=>$content->menus(),'isPreview'=>false]);}
}
