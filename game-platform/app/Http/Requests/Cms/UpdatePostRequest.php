<?php

declare(strict_types=1);

namespace App\Http\Requests\Cms;

use App\Enums\ContentStatus;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class UpdatePostRequest extends FormRequest
{
    public function authorize(): bool { return (bool)$this->user()?->can('news.manage'); }
    public function rules(): array
    {
        $id=$this->route('post')?->id;
        return ['title'=>['required','string','max:180'],'slug'=>['required','alpha_dash','max:180',Rule::unique('posts','slug')->ignore($id)],'post_category_id'=>['nullable','exists:post_categories,id'],'status'=>['required',Rule::enum(ContentStatus::class)],'excerpt'=>['nullable','string','max:500'],'body'=>['required','string'],'author_name'=>['required','string','max:120'],'published_at'=>['nullable','date'],'reading_minutes'=>['required','integer','min:1','max:240'],'seo_title'=>['nullable','string','max:180'],'seo_description'=>['nullable','string','max:320']];
    }
}
