<?php

declare(strict_types=1);

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Media extends Model
{
    protected $table = 'media';
    protected $fillable = ['name','disk','path','url','alt_text','mime_type','width','height','metadata'];
    protected function casts(): array { return ['metadata' => 'array']; }
}
