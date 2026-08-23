<?php

declare(strict_types=1);
namespace App\Http\Requests\Catalog;
use App\Enums\ContentStatus; use App\Enums\GameReleaseStatus; use Illuminate\Foundation\Http\FormRequest; use Illuminate\Validation\Rule;
class SaveGameRequest extends FormRequest
{
    public function authorize(): bool{return true;}
    public function rules(): array
    {
        $game=$this->route('game');
        return ['title'=>['required','string','max:255'],'slug'=>['required','string','max:255',Rule::unique('games','slug')->ignore($game?->id)],'short_description'=>['nullable','string','max:500'],'description'=>['nullable','string'],'developer_name'=>['nullable','string','max:255'],'publisher_name'=>['nullable','string','max:255'],'publication_status'=>['required',Rule::enum(ContentStatus::class)],'published_at'=>['nullable','date'],'release_status'=>['required',Rule::enum(GameReleaseStatus::class)],'release_date'=>['nullable','date'],'featured_rank'=>['nullable','integer','min:1','max:999'],'age_rating'=>['nullable','string','max:40'],'languages_text'=>['nullable','string','max:500'],'cover_url'=>['nullable','url','max:2048'],'hero_url'=>['nullable','url','max:2048'],'accent'=>['nullable','string','max:20'],'website_url'=>['nullable','url','max:2048'],'press_kit_url'=>['nullable','url','max:2048'],'support_url'=>['nullable','url','max:2048'],'seo_title'=>['nullable','string','max:255'],'seo_description'=>['nullable','string','max:320'],'primary_genre_id'=>['nullable','exists:genres,id'],'genre_ids'=>['array'],'genre_ids.*'=>['integer','exists:genres,id'],'tag_ids'=>['array'],'tag_ids.*'=>['integer','exists:tags,id'],'platform_ids'=>['array'],'platform_ids.*'=>['integer','exists:platforms,id'],'mode_ids'=>['array'],'mode_ids.*'=>['integer','exists:game_modes,id'],'accessibility_ids'=>['array'],'accessibility_ids.*'=>['integer','exists:accessibility_features,id'],'price_minor'=>['nullable','integer','min:0'],'currency'=>['nullable','string','size:3'],'trailer_embed_url'=>['nullable','url','max:2048'],'screenshot_urls'=>['nullable','string'],'release_version'=>['nullable','string','max:80'],'release_file_size_gb'=>['nullable','numeric','min:0'],'minimum_os'=>['nullable','string','max:255'],'minimum_cpu'=>['nullable','string','max:255'],'minimum_memory'=>['nullable','string','max:255'],'minimum_gpu'=>['nullable','string','max:255'],'minimum_storage'=>['nullable','string','max:255'],'recommended_os'=>['nullable','string','max:255'],'recommended_cpu'=>['nullable','string','max:255'],'recommended_memory'=>['nullable','string','max:255'],'recommended_gpu'=>['nullable','string','max:255'],'recommended_storage'=>['nullable','string','max:255']];
    }
}
