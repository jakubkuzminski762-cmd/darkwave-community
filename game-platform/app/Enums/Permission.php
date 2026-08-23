<?php

declare(strict_types=1);

namespace App\Enums;

enum Permission: string
{
    case AdminAccess = 'admin.access';
    case SettingsManage = 'settings.manage';
    case IntegrationsManage = 'integrations.manage';
    case RolesManage = 'roles.manage';
    case AuditView = 'audit.view';

    case CmsManage = 'cms.manage';
    case GamesManage = 'games.manage';
    case NewsManage = 'news.manage';
    case MediaManage = 'media.manage';

    case SubmissionsManage = 'submissions.manage';
    case StudiosManage = 'studios.manage';
    case PublishingProjectsManage = 'publishing-projects.manage';
    case PublishingMessagesManage = 'publishing-messages.manage';
    case PublishingDocumentsManage = 'publishing-documents.manage';

    case SupportManage = 'support.manage';

    case OrdersView = 'orders.view';
    case OrdersManage = 'orders.manage';
    case LicensesView = 'licenses.view';
    case LicensesManage = 'licenses.manage';
    case RefundsManage = 'refunds.manage';
    case FinanceManage = 'finance.manage';

    case ReportsView = 'reports.view';
    case AnalyticsView = 'analytics.view';

    public function label(): string
    {
        return str($this->value)
            ->replace(['.', '-'], ' ')
            ->headline()
            ->toString();
    }
}
