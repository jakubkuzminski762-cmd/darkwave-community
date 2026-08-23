<?php

declare(strict_types=1);

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class PostCategory extends Model
{
    protected $fillable = ['name','slug','sort_order'];
    public function posts(): HasMany { return $this->hasMany(Post::class); }
}
