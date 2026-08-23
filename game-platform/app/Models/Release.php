<?php

declare(strict_types=1);
namespace App\Models; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsTo; use Illuminate\Database\Eloquent\Relations\HasMany;
class Release extends Model { protected $fillable=['game_id','game_edition_id','platform_id','status','version','release_date','file_size_bytes']; protected function casts():array{return['release_date'=>'date'];} public function game():BelongsTo{return $this->belongsTo(Game::class);} public function edition():BelongsTo{return $this->belongsTo(GameEdition::class,'game_edition_id');} public function platform():BelongsTo{return $this->belongsTo(Platform::class);} public function notes():HasMany{return $this->hasMany(ReleaseNote::class);} }
