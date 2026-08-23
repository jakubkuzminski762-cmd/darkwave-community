<?php

declare(strict_types=1);
namespace App\Models;
use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsToMany;
class AccessibilityFeature extends Model { protected $fillable=['name','slug','description']; public function games(): BelongsToMany { return $this->belongsToMany(Game::class,'game_accessibility_feature'); } }
