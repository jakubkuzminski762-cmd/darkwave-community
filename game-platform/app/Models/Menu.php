<?php

declare(strict_types=1);

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Menu extends Model
{
    protected $fillable = ['name','location','items','is_enabled'];
    protected function casts(): array { return ['items' => 'array', 'is_enabled' => 'boolean']; }
}
