<?php

declare(strict_types=1);
namespace Database\Factories; use App\Enums\ContentStatus; use App\Enums\GameReleaseStatus; use App\Models\Game; use Illuminate\Database\Eloquent\Factories\Factory; use Illuminate\Support\Str;
class GameFactory extends Factory { protected $model=Game::class; public function definition():array{$title=fake()->unique()->words(3,true);return['title'=>ucwords($title),'slug'=>Str::slug($title),'short_description'=>fake()->sentence(),'description'=>fake()->paragraphs(2,true),'developer_name'=>fake()->company(),'publisher_name'=>'Game Studio Platform','publication_status'=>ContentStatus::Published,'published_at'=>now()->subDay(),'release_status'=>GameReleaseStatus::ComingSoon,'release_date'=>now()->addYear()->toDateString()];} public function draft():static{return $this->state(fn()=>['publication_status'=>ContentStatus::Draft,'release_status'=>GameReleaseStatus::Draft,'published_at'=>null]);} }
