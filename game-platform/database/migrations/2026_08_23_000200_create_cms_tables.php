<?php

declare(strict_types=1);

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('media', function (Blueprint $table): void {
            $table->id(); $table->string('name'); $table->string('disk')->default('public'); $table->string('path')->nullable(); $table->string('url')->nullable(); $table->string('alt_text')->nullable(); $table->string('mime_type')->nullable(); $table->unsignedInteger('width')->nullable(); $table->unsignedInteger('height')->nullable(); $table->json('metadata')->nullable(); $table->timestamps();
        });
        Schema::create('pages', function (Blueprint $table): void {
            $table->id(); $table->string('title'); $table->string('slug')->unique(); $table->string('status',20)->default('draft')->index(); $table->string('template')->default('default'); $table->text('excerpt')->nullable(); $table->longText('body')->nullable(); $table->string('seo_title')->nullable(); $table->string('seo_description',320)->nullable(); $table->string('og_image_url')->nullable(); $table->string('canonical_url')->nullable(); $table->timestamp('published_at')->nullable()->index(); $table->timestamps();
        });
        Schema::create('page_blocks', function (Blueprint $table): void {
            $table->id(); $table->foreignId('page_id')->constrained()->cascadeOnDelete(); $table->string('type'); $table->unsignedInteger('position')->default(0); $table->json('payload')->nullable(); $table->boolean('is_enabled')->default(true); $table->timestamps(); $table->index(['page_id','position']);
        });
        Schema::create('post_categories', function (Blueprint $table): void {
            $table->id(); $table->string('name'); $table->string('slug')->unique(); $table->unsignedInteger('sort_order')->default(0); $table->timestamps();
        });
        Schema::create('posts', function (Blueprint $table): void {
            $table->id(); $table->foreignId('post_category_id')->nullable()->constrained()->nullOnDelete(); $table->foreignId('media_id')->nullable()->constrained('media')->nullOnDelete(); $table->string('title'); $table->string('slug')->unique(); $table->text('excerpt')->nullable(); $table->longText('body'); $table->string('author_name')->default('Studio'); $table->string('status',20)->default('draft')->index(); $table->timestamp('published_at')->nullable()->index(); $table->unsignedSmallInteger('reading_minutes')->default(1); $table->json('toc')->nullable(); $table->json('embedded_media')->nullable(); $table->string('related_game_slug')->nullable(); $table->string('seo_title')->nullable(); $table->string('seo_description',320)->nullable(); $table->string('og_image_url')->nullable(); $table->string('canonical_url')->nullable(); $table->timestamps();
        });
        Schema::create('menus', function (Blueprint $table): void {
            $table->id(); $table->string('name'); $table->string('location')->unique(); $table->json('items'); $table->boolean('is_enabled')->default(true); $table->timestamps();
        });
    }
    public function down(): void
    {
        Schema::dropIfExists('menus'); Schema::dropIfExists('posts'); Schema::dropIfExists('post_categories'); Schema::dropIfExists('page_blocks'); Schema::dropIfExists('pages'); Schema::dropIfExists('media');
    }
};
