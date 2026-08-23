<?php

declare(strict_types=1);
namespace App\Models;
use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsToMany;
class Platform extends Model { protected $fillable=['name','slug','sort_order']; public function games(): BelongsToMany { return $this->belongsToMany(Game::class,'game_platform'); } }
