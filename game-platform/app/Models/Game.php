<?php

declare(strict_types=1);
namespace App\Models;
use App\Enums\ContentStatus; use App\Enums\GameReleaseStatus; use Illuminate\Database\Eloquent\Builder; use Illuminate\Database\Eloquent\Factories\HasFactory; use Illuminate\Database\Eloquent\Model; use Illuminate\Database\Eloquent\Relations\BelongsToMany; use Illuminate\Database\Eloquent\Relations\HasMany; use Illuminate\Database\Eloquent\Relations\HasOne;
class Game extends Model
{
    use HasFactory;
    protected $fillable=['title','slug','short_description','description','developer_name','publisher_name','publication_status','published_at','release_status','release_date','featured_rank','age_rating','languages','cover_url','hero_url','accent','website_url','press_kit_url','support_url','seo_title','seo_description','canonical_url'];
    protected function casts(): array { return ['publication_status'=>ContentStatus::class,'published_at'=>'datetime','release_status'=>GameReleaseStatus::class,'release_date'=>'date','languages'=>'array']; }
    public function getRouteKeyName(): string { return 'slug'; }
    public function genres(): BelongsToMany { return $this->belongsToMany(Genre::class,'game_genre')->withPivot('is_primary'); }
    public function primaryGenre(): BelongsToMany { return $this->genres()->wherePivot('is_primary',true); }
    public function tags(): BelongsToMany { return $this->belongsToMany(Tag::class,'game_tag'); }
    public function platforms(): BelongsToMany { return $this->belongsToMany(Platform::class,'game_platform'); }
    public function modes(): BelongsToMany { return $this->belongsToMany(GameMode::class,'game_mode'); }
    public function accessibilityFeatures(): BelongsToMany { return $this->belongsToMany(AccessibilityFeature::class,'game_accessibility_feature'); }
    public function features(): HasMany { return $this->hasMany(GameFeature::class)->orderBy('sort_order'); }
    public function media(): HasMany { return $this->hasMany(GameMedia::class)->orderBy('position'); }
    public function trailers(): HasMany { return $this->hasMany(Trailer::class)->orderByDesc('is_primary')->orderBy('position'); }
    public function primaryTrailer(): HasOne { return $this->hasOne(Trailer::class)->where('is_primary',true); }
    public function editions(): HasMany { return $this->hasMany(GameEdition::class); }
    public function releases(): HasMany { return $this->hasMany(Release::class)->orderByDesc('release_date'); }
    public function systemRequirements(): HasMany { return $this->hasMany(SystemRequirement::class); }
    public function posts(): HasMany { return $this->hasMany(Post::class); }
    public function scopePubliclyVisible(Builder $query): Builder { return $query->where(function(Builder $q):void{$q->where(fn(Builder $published)=>$published->where('publication_status',ContentStatus::Published->value)->where(fn(Builder $date)=>$date->whereNull('published_at')->orWhere('published_at','<=',now())))->orWhere(fn(Builder $scheduled)=>$scheduled->where('publication_status',ContentStatus::Scheduled->value)->whereNotNull('published_at')->where('published_at','<=',now()));})->where('release_status','!=',GameReleaseStatus::Draft->value); }
    public function priceLabel(): string { $edition=$this->relationLoaded('editions')?$this->editions->whereNotNull('price_minor')->sortBy('price_minor')->first():$this->editions()->whereNotNull('price_minor')->orderBy('price_minor')->first(); if(!$edition)return 'Cena wkrótce'; $amount=number_format($edition->price_minor/100,2,',',' '); return $edition->currency==='PLN'?$amount.' zł':$amount.' '.$edition->currency; }
}
