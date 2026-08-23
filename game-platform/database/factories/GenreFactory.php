<?php

declare(strict_types=1);
namespace Database\Factories; use App\Models\Genre; use Illuminate\Database\Eloquent\Factories\Factory; use Illuminate\Support\Str;
class GenreFactory extends Factory { protected $model=Genre::class; public function definition():array{$name=fake()->unique()->words(2,true);return['name'=>ucwords($name),'slug'=>Str::slug($name),'group_name'=>'Test','sort_order'=>0];} }
