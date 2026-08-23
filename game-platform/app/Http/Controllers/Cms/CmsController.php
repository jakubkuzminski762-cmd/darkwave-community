<?php

declare(strict_types=1);

namespace App\Http\Controllers\Cms;

use App\Http\Controllers\Controller;
use App\Http\Requests\Cms\UpdatePageRequest;
use App\Http\Requests\Cms\UpdatePostRequest;
use App\Models\Media;
use App\Models\Page;
use App\Models\Post;
use App\Models\PostCategory;
use Illuminate\Http\RedirectResponse;
use Illuminate\Support\Facades\URL;
use Illuminate\View\View;

class CmsController extends Controller
{
    public function index(): View { return view('admin.cms.index',['pages'=>Page::query()->orderBy('title')->get(),'posts'=>Post::query()->with('category')->orderByDesc('updated_at')->limit(20)->get(),'media'=>Media::query()->latest()->limit(20)->get()]); }
    public function editPage(Page $page): View { return view('admin.cms.edit-page',['page'=>$page->load('blocks'),'previewUrl'=>URL::temporarySignedRoute('preview.page',now()->addHour(),['page'=>$page])]); }
    public function updatePage(UpdatePageRequest $request, Page $page): RedirectResponse { $page->update($request->validated()); return back()->with('status','Strona została zapisana.'); }
    public function editPost(Post $post): View { return view('admin.cms.edit-post',['post'=>$post,'categories'=>PostCategory::query()->orderBy('sort_order')->get(),'previewUrl'=>URL::temporarySignedRoute('preview.post',now()->addHour(),['post'=>$post])]); }
    public function updatePost(UpdatePostRequest $request, Post $post): RedirectResponse { $post->update($request->validated()); return back()->with('status','Aktualność została zapisana.'); }
}
