<?php

declare(strict_types=1);

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('genres', function (Blueprint $table): void {
            $table->id(); $table->string('name'); $table->string('slug')->unique(); $table->string('group_name')->nullable(); $table->text('description')->nullable(); $table->unsignedInteger('sort_order')->default(0); $table->timestamps();
        });
        Schema::create('tags', function (Blueprint $table): void {
            $table->id(); $table->string('name'); $table->string('slug')->unique(); $table->text('description')->nullable(); $table->timestamps();
        });
        Schema::create('platforms', function (Blueprint $table): void {
            $table->id(); $table->string('name'); $table->string('slug')->unique(); $table->unsignedInteger('sort_order')->default(0); $table->timestamps();
        });
        Schema::create('game_modes', function (Blueprint $table): void {
            $table->id(); $table->string('name'); $table->string('slug')->unique(); $table->unsignedInteger('sort_order')->default(0); $table->timestamps();
        });
        Schema::create('accessibility_features', function (Blueprint $table): void {
            $table->id(); $table->string('name'); $table->string('slug')->unique(); $table->text('description')->nullable(); $table->timestamps();
        });
        Schema::create('games', function (Blueprint $table): void {
            $table->id();
            $table->string('title'); $table->string('slug')->unique();
            $table->text('short_description')->nullable(); $table->longText('description')->nullable();
            $table->string('developer_name')->nullable(); $table->string('publisher_name')->nullable();
            $table->string('publication_status', 20)->default('draft')->index(); $table->timestamp('published_at')->nullable()->index();
            $table->string('release_status', 24)->default('draft')->index(); $table->date('release_date')->nullable()->index();
            $table->unsignedSmallInteger('featured_rank')->nullable()->index(); $table->string('age_rating', 40)->nullable();
            $table->json('languages')->nullable(); $table->string('cover_url')->nullable(); $table->string('hero_url')->nullable(); $table->string('accent', 20)->default('mint');
            $table->string('website_url')->nullable(); $table->string('press_kit_url')->nullable(); $table->string('support_url')->nullable();
            $table->string('seo_title')->nullable(); $table->string('seo_description', 320)->nullable(); $table->string('canonical_url')->nullable();
            $table->timestamps();
            $table->index(['publication_status','release_status','release_date']);
        });
        Schema::create('game_genre', function (Blueprint $table): void {
            $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->foreignId('genre_id')->constrained()->cascadeOnDelete(); $table->boolean('is_primary')->default(false); $table->unique(['game_id','genre_id']); $table->index(['genre_id','is_primary']);
        });
        Schema::create('game_tag', function (Blueprint $table): void {
            $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->foreignId('tag_id')->constrained()->cascadeOnDelete(); $table->unique(['game_id','tag_id']);
        });
        Schema::create('game_platform', function (Blueprint $table): void {
            $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->foreignId('platform_id')->constrained()->cascadeOnDelete(); $table->unique(['game_id','platform_id']);
        });
        Schema::create('game_mode', function (Blueprint $table): void {
            $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->foreignId('game_mode_id')->constrained()->cascadeOnDelete(); $table->unique(['game_id','game_mode_id']);
        });
        Schema::create('game_accessibility_feature', function (Blueprint $table): void {
            $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->foreignId('accessibility_feature_id')->constrained()->cascadeOnDelete(); $table->unique(['game_id','accessibility_feature_id'],'game_accessibility_unique');
        });
        Schema::create('game_features', function (Blueprint $table): void {
            $table->id(); $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->string('name'); $table->string('slug'); $table->text('description')->nullable(); $table->unsignedInteger('sort_order')->default(0); $table->timestamps(); $table->unique(['game_id','slug']);
        });
        Schema::create('game_media', function (Blueprint $table): void {
            $table->id(); $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->foreignId('media_id')->constrained('media')->cascadeOnDelete(); $table->string('type', 30)->default('screenshot'); $table->unsignedInteger('position')->default(0); $table->timestamps(); $table->unique(['game_id','media_id']); $table->index(['game_id','type','position']);
        });
        Schema::create('trailers', function (Blueprint $table): void {
            $table->id(); $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->string('title'); $table->string('provider')->default('embed'); $table->string('embed_url')->nullable(); $table->string('thumbnail_url')->nullable(); $table->boolean('is_primary')->default(false); $table->unsignedInteger('position')->default(0); $table->timestamps();
        });
        Schema::create('game_editions', function (Blueprint $table): void {
            $table->id(); $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->string('name'); $table->string('slug'); $table->text('description')->nullable(); $table->boolean('is_default')->default(false); $table->unsignedInteger('price_minor')->nullable(); $table->char('currency',3)->default('PLN'); $table->json('includes')->nullable(); $table->timestamps(); $table->unique(['game_id','slug']);
        });
        Schema::create('releases', function (Blueprint $table): void {
            $table->id(); $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->foreignId('game_edition_id')->nullable()->constrained()->nullOnDelete(); $table->foreignId('platform_id')->nullable()->constrained()->nullOnDelete(); $table->string('status',24)->default('coming_soon')->index(); $table->string('version')->nullable(); $table->date('release_date')->nullable()->index(); $table->unsignedBigInteger('file_size_bytes')->nullable(); $table->timestamps();
        });
        Schema::create('release_notes', function (Blueprint $table): void {
            $table->id(); $table->foreignId('release_id')->constrained()->cascadeOnDelete(); $table->string('title'); $table->longText('body'); $table->timestamp('published_at')->nullable(); $table->timestamps();
        });
        Schema::create('system_requirements', function (Blueprint $table): void {
            $table->id(); $table->foreignId('game_id')->constrained()->cascadeOnDelete(); $table->foreignId('platform_id')->nullable()->constrained()->nullOnDelete(); $table->string('type',20); $table->string('os')->nullable(); $table->string('cpu')->nullable(); $table->string('memory')->nullable(); $table->string('gpu')->nullable(); $table->string('storage')->nullable(); $table->string('directx')->nullable(); $table->text('notes')->nullable(); $table->timestamps(); $table->unique(['game_id','platform_id','type'],'requirements_game_platform_type_unique');
        });
        Schema::table('posts', function (Blueprint $table): void {
            $table->foreignId('game_id')->nullable()->constrained('games')->nullOnDelete();
        });
    }

    public function down(): void
    {
        Schema::table('posts', function (Blueprint $table): void { $table->dropConstrainedForeignId('game_id'); });
        Schema::dropIfExists('system_requirements'); Schema::dropIfExists('release_notes'); Schema::dropIfExists('releases'); Schema::dropIfExists('game_editions'); Schema::dropIfExists('trailers'); Schema::dropIfExists('game_media'); Schema::dropIfExists('game_features'); Schema::dropIfExists('game_accessibility_feature'); Schema::dropIfExists('game_mode'); Schema::dropIfExists('game_platform'); Schema::dropIfExists('game_tag'); Schema::dropIfExists('game_genre'); Schema::dropIfExists('games'); Schema::dropIfExists('accessibility_features'); Schema::dropIfExists('game_modes'); Schema::dropIfExists('platforms'); Schema::dropIfExists('tags'); Schema::dropIfExists('genres');
    }
};
