<?php

declare(strict_types=1);
namespace App\Models; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsTo;
class ReleaseNote extends Model { protected $fillable=['release_id','title','body','published_at']; protected function casts():array{return['published_at'=>'datetime'];} public function release():BelongsTo{return $this->belongsTo(Release::class);} }
