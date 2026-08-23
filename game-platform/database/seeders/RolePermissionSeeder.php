<?php

declare(strict_types=1);

namespace Database\Seeders;

use App\Enums\AdminRole;
use App\Enums\Permission as PermissionEnum;
use App\Models\Permission;
use App\Models\Role;
use Illuminate\Database\Seeder;
use Illuminate\Support\Collection;

class RolePermissionSeeder extends Seeder
{
    public function run(): void
    {
        /** @var Collection<string, Permission> $permissions */
        $permissions = collect(PermissionEnum::cases())
            ->mapWithKeys(function (PermissionEnum $permission): array {
                $model = Permission::query()->updateOrCreate(
                    ['slug' => $permission->value],
                    ['name' => $permission->label()],
                );

                return [$permission->value => $model];
            });

        $matrix = $this->matrix();

        foreach (AdminRole::cases() as $roleEnum) {
            $role = Role::query()->updateOrCreate(
                ['slug' => $roleEnum->value],
                ['name' => $roleEnum->label()],
            );

            $permissionIds = collect($matrix[$roleEnum->value] ?? [])
                ->map(fn (PermissionEnum $permission): int => $permissions[$permission->value]->id)
                ->all();

            $role->permissions()->sync($permissionIds);
        }
    }

    /**
     * @return array<string, list<PermissionEnum>>
     */
    private function matrix(): array
    {
        $all = PermissionEnum::cases();

        return [
            AdminRole::SuperAdmin->value => $all,

            AdminRole::Administrator->value => array_values(array_filter(
                $all,
                fn (PermissionEnum $permission): bool => ! in_array(
                    $permission,
                    [PermissionEnum::IntegrationsManage, PermissionEnum::RolesManage],
                    true,
                ),
            )),

            AdminRole::Editor->value => [
                PermissionEnum::AdminAccess,
                PermissionEnum::CmsManage,
                PermissionEnum::GamesManage,
                PermissionEnum::NewsManage,
                PermissionEnum::MediaManage,
            ],

            AdminRole::PublisherManager->value => [
                PermissionEnum::AdminAccess,
                PermissionEnum::SubmissionsManage,
                PermissionEnum::StudiosManage,
                PermissionEnum::PublishingProjectsManage,
                PermissionEnum::PublishingMessagesManage,
                PermissionEnum::PublishingDocumentsManage,
            ],

            AdminRole::SupportAgent->value => [
                PermissionEnum::AdminAccess,
                PermissionEnum::SupportManage,
                PermissionEnum::OrdersView,
                PermissionEnum::LicensesView,
            ],

            AdminRole::Finance->value => [
                PermissionEnum::AdminAccess,
                PermissionEnum::OrdersView,
                PermissionEnum::OrdersManage,
                PermissionEnum::LicensesView,
                PermissionEnum::RefundsManage,
                PermissionEnum::FinanceManage,
                PermissionEnum::ReportsView,
            ],

            AdminRole::Analyst->value => [
                PermissionEnum::AdminAccess,
                PermissionEnum::ReportsView,
                PermissionEnum::AnalyticsView,
            ],
        ];
    }
}
