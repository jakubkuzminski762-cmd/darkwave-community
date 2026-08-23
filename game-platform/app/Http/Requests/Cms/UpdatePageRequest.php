<?php

declare(strict_types=1);

namespace App\Http\Requests\Cms;

use App\Enums\ContentStatus;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class UpdatePageRequest extends FormRequest
{
    public function authorize(): bool { return (bool)$this->user()?->can('cms.manage'); }
    public function rules(): array
    {
        $id=$this->route('page')?->id;
        return ['title'=>['required','string','max:180'],'slug'=>['required','alpha_dash','max:180',Rule::unique('pages','slug')->ignore($id)],'status'=>['required',Rule::enum(ContentStatus::class)],'excerpt'=>['nullable','string','max:500'],'body'=>['nullable','string'],'published_at'=>['nullable','date'],'seo_title'=>['nullable','string','max:180'],'seo_description'=>['nullable','string','max:320']];
    }
}
