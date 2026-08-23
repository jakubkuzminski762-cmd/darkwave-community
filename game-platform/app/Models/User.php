<?php

declare(strict_types=1);

namespace App\Models;

use App\Enums\AdminRole;
use App\Enums\Permission as PermissionEnum;
use Database\Factories\UserFactory;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;

class User extends Authenticatable
{
    /** @use HasFactory<UserFactory> */
    use HasFactory, Notifiable;

    protected $fillable = [
        'name',
        'email',
        'password',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
        ];
    }

    /**
     * @return BelongsToMany<Role, $this>
     */
    public function roles(): BelongsToMany
    {
        return $this->belongsToMany(Role::class, 'role_user');
    }

    public function hasRole(AdminRole|string $role): bool
    {
        $slug = $role instanceof AdminRole ? $role->value : $role;

        return $this->roles()->where('slug', $slug)->exists();
    }

    public function hasPermission(PermissionEnum|string $permission): bool
    {
        if ($this->hasRole(AdminRole::SuperAdmin)) {
            return true;
        }

        $slug = $permission instanceof PermissionEnum ? $permission->value : $permission;

        return $this->roles()
            ->whereHas('permissions', fn ($query) => $query->where('slug', $slug))
            ->exists();
    }
}
