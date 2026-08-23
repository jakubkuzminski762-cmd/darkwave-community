<?php

declare(strict_types=1);
namespace App\Models;
use App\Enums\ContentStatus; use Illuminate\Database\Eloquent\Builder; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsTo;
class Post extends Model
{
    protected $fillable=['post_category_id','media_id','game_id','title','slug','excerpt','body','author_name','status','published_at','reading_minutes','toc','embedded_media','related_game_slug','seo_title','seo_description','og_image_url','canonical_url'];
    protected function casts():array{return['status'=>ContentStatus::class,'published_at'=>'datetime','toc'=>'array','embedded_media'=>'array'];}
    public function category():BelongsTo{return $this->belongsTo(PostCategory::class,'post_category_id');} public function media():BelongsTo{return $this->belongsTo(Media::class);} public function game():BelongsTo{return $this->belongsTo(Game::class);}
    public function scopePubliclyVisible(Builder $query):Builder{return $query->where(function(Builder $q):void{$q->where(fn(Builder $published)=>$published->where('status',ContentStatus::Published->value)->where(fn(Builder $date)=>$date->whereNull('published_at')->orWhere('published_at','<=',now())))->orWhere(fn(Builder $scheduled)=>$scheduled->where('status',ContentStatus::Scheduled->value)->whereNotNull('published_at')->where('published_at','<=',now()));});}
}
