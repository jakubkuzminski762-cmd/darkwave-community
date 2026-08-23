<?php

declare(strict_types=1);
namespace App\Models; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsTo;
class GameFeature extends Model { protected $fillable=['game_id','name','slug','description','sort_order']; public function game(): BelongsTo { return $this->belongsTo(Game::class); } }
