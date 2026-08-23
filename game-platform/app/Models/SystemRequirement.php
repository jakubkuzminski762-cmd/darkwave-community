<?php

declare(strict_types=1);
namespace App\Models; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsTo;
class SystemRequirement extends Model { protected $fillable=['game_id','platform_id','type','os','cpu','memory','gpu','storage','directx','notes']; public function game():BelongsTo{return $this->belongsTo(Game::class);} public function platform():BelongsTo{return $this->belongsTo(Platform::class);} }
