<?php

declare(strict_types=1);
namespace App\Models; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsTo;
class GameMedia extends Model { protected $table='game_media'; protected $fillable=['game_id','media_id','type','position']; public function game(): BelongsTo{return $this->belongsTo(Game::class);} public function media(): BelongsTo{return $this->belongsTo(Media::class);} }
