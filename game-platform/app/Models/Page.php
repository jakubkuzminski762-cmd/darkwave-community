<?php

declare(strict_types=1);

namespace App\Models;

use App\Enums\ContentStatus;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Page extends Model
{
    protected $fillable = ['title','slug','status','template','excerpt','body','seo_title','seo_description','og_image_url','canonical_url','published_at'];

    protected function casts(): array
    {
        return ['status' => ContentStatus::class, 'published_at' => 'datetime'];
    }

    public function blocks(): HasMany
    {
        return $this->hasMany(PageBlock::class)->orderBy('position');
    }

    public function scopePubliclyVisible(Builder $query): Builder
    {
        return $query->where(function (Builder $q): void {
            $q->where(fn (Builder $published) => $published->where('status', ContentStatus::Published->value)->where(fn (Builder $date) => $date->whereNull('published_at')->orWhere('published_at', '<=', now())))
              ->orWhere(fn (Builder $scheduled) => $scheduled->where('status', ContentStatus::Scheduled->value)->whereNotNull('published_at')->where('published_at', '<=', now()));
        });
    }
}
