<?php

declare(strict_types=1);
namespace App\Models; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsTo; use Illuminate\Database\Eloquent\Relations\HasMany;
class GameEdition extends Model { protected $fillable=['game_id','name','slug','description','is_default','price_minor','currency','includes']; protected function casts():array{return['is_default'=>'boolean','includes'=>'array'];} public function game():BelongsTo{return $this->belongsTo(Game::class);} public function releases():HasMany{return $this->hasMany(Release::class);} }
