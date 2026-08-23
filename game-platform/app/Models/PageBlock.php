<?php

declare(strict_types=1);

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class PageBlock extends Model
{
    protected $fillable = ['page_id','type','position','payload','is_enabled'];
    protected function casts(): array { return ['payload' => 'array', 'is_enabled' => 'boolean']; }
    public function page(): BelongsTo { return $this->belongsTo(Page::class); }
}
