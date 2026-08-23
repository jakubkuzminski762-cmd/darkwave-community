<?php

declare(strict_types=1);
namespace App\Models;
use Illuminate\Database\Eloquent\Factories\HasFactory; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsToMany;
class Genre extends Model { use HasFactory; protected $fillable=['name','slug','group_name','description','sort_order']; public function games(): BelongsToMany { return $this->belongsToMany(Game::class,'game_genre')->withPivot('is_primary'); } public function getRouteKeyName(): string { return 'slug'; } }
