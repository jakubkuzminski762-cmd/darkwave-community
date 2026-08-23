<?php

declare(strict_types=1);

namespace App\Http\Controllers\Public;

use App\Http\Controllers\Controller;
use App\Services\Cms\PublicContentService;
use Illuminate\View\View;

class HomeController extends Controller
{
    public function __invoke(PublicContentService $content): View { return view('public.home', $content->homepage()); }
}
