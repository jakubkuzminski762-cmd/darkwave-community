<?php

declare(strict_types=1);
namespace App\Http\Controllers\Catalog;
use App\Http\Controllers\Controller; use App\Models\AccessibilityFeature; use App\Models\GameMode; use App\Models\Genre; use App\Models\Platform; use App\Models\Tag; use Illuminate\Http\RedirectResponse; use Illuminate\Http\Request; use Illuminate\Validation\Rule; use Illuminate\View\View;
class AdminDictionaryController extends Controller
{
    public function index():View{return view('admin.catalog.dictionaries',['genres'=>Genre::query()->orderBy('sort_order')->get(),'tags'=>Tag::query()->orderBy('name')->get(),'platforms'=>Platform::query()->orderBy('sort_order')->get(),'modes'=>GameMode::query()->orderBy('sort_order')->get(),'accessibility'=>AccessibilityFeature::query()->orderBy('name')->get()]);}
    public function store(Request $request):RedirectResponse{$type=$request->string('type')->toString();$map=['genre'=>[Genre::class,'genres'],'tag'=>[Tag::class,'tags'],'platform'=>[Platform::class,'platforms'],'mode'=>[GameMode::class,'game_modes'],'accessibility'=>[AccessibilityFeature::class,'accessibility_features']];abort_unless(isset($map[$type]),422);[$model,$table]=$map[$type];$data=$request->validate(['name'=>['required','string','max:255'],'slug'=>['required','string','max:255',Rule::unique($table,'slug')],'group_name'=>['nullable','string','max:255']]);$payload=['name'=>$data['name'],'slug'=>$data['slug']];if($type==='genre')$payload['group_name']=$data['group_name']??null;$model::query()->create($payload);return back()->with('status','Dodano pozycję słownika.');}
}
