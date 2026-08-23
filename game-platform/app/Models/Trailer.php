<?php

declare(strict_types=1);
namespace App\Models; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsTo;
class Trailer extends Model { protected $fillable=['game_id','title','provider','embed_url','thumbnail_url','is_primary','position']; protected function casts(): array{return['is_primary'=>'boolean'];} public function game(): BelongsTo{return $this->belongsTo(Game::class);} }
