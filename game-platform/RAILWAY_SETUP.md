# Railway staging

This application is intended to be deployed from the `game-platform` Git branch with Railway Root Directory set to `/game-platform`.

## Services
- App: Laravel application from GitHub.
- MySQL: Railway MySQL database service.

## Required app variables
- APP_NAME=Game Studio Platform
- APP_ENV=staging
- APP_KEY=<secret generated for Railway>
- APP_DEBUG=false
- APP_URL=https://<generated-domain>
- APP_LOCALE=pl
- APP_FALLBACK_LOCALE=en
- DB_CONNECTION=mysql
- DB_HOST=${{MySQL.MYSQLHOST}}
- DB_PORT=${{MySQL.MYSQLPORT}}
- DB_DATABASE=${{MySQL.MYSQLDATABASE}}
- DB_USERNAME=${{MySQL.MYSQLUSER}}
- DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
- SESSION_DRIVER=database
- SESSION_ENCRYPT=true
- SESSION_SECURE_COOKIE=true
- CACHE_STORE=database
- QUEUE_CONNECTION=database
- LOG_CHANNEL=stack
- LOG_LEVEL=info
- MAIL_MAILER=log

Do not commit APP_KEY or database credentials.
