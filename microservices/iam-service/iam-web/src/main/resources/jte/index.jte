@import kh.edu.istad.platform.iamweb.i18n.Messages
@param Messages messages
@param kh.edu.istad.platform.iamdomaincore.model.User user
@param java.util.List<String> roles
@param java.util.List<String> permissions
@param org.springframework.security.web.csrf.CsrfToken csrfToken
<!DOCTYPE html>
<html lang="${messages.language()}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("profile.title")} · ${messages.get("app.name")}</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<main class="page">
    <div class="meta">
        <div class="brand">
            <span class="brand__mark">D</span>
            <span>
                <span class="brand__name">${messages.get("app.name")}</span><br>
                <span class="brand__tagline">${messages.get("app.tagline")}</span>
            </span>
        </div>
        <nav class="lang">
            @if(messages.isEnglish())
            <a class="is-active" href="/?lang=en">${messages.get("common.english")}</a>
            <a href="/?lang=km">${messages.get("common.khmer")}</a>
            @else
            <a href="/?lang=en">${messages.get("common.english")}</a>
            <a class="is-active" href="/?lang=km">${messages.get("common.khmer")}</a>
            @endif
        </nav>
    </div>

    <section class="card">
        <h1>${messages.get("profile.title")}</h1>
        <p class="subtitle">${messages.get("profile.claims")}</p>

        <dl class="claims">
            <dt>${messages.get("profile.username")}</dt>
            <dd>${user.username()}</dd>

            <dt>${messages.get("profile.email")}</dt>
            <dd>${user.email()}</dd>

            <dt>${messages.get("profile.emailVerified")}</dt>
            <dd>${user.emailVerified() ? "✓" : "—"}</dd>

            <dt>${messages.get("profile.name")}</dt>
            <dd>${user.name()}</dd>

            <dt>${messages.get("profile.locale")}</dt>
            <dd>${user.locale()}</dd>
        </dl>

        <div class="nav">
            <a class="button button--secondary" href="/profile/edit">${messages.get("profile.edit")}</a>
            <a class="button button--secondary" href="/profile/password">${messages.get("profile.changePassword")}</a>
            <a class="button button--secondary" href="/profile/accounts">${messages.get("profile.linkedAccounts")}</a>
            <a class="button button--secondary" href="/profile/apps">${messages.get("profile.connectedApps")}</a>
            <form method="post" action="/logout">
                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                <button type="submit" class="secondary">${messages.get("common.logout")}</button>
            </form>
        </div>
    </section>

    <section class="card">
        <h2>${messages.get("profile.roles")}</h2>
        <div class="chips">
            @if(roles.isEmpty())
            <span class="chip chip--muted">${messages.get("profile.noRoles")}</span>
            @else
            @for(String role : roles)
            <span class="chip">${role}</span>
            @endfor
            @endif
        </div>
    </section>

    <section class="card">
        <h2>${messages.get("profile.permissions")}</h2>
        <div class="chips">
            @if(permissions.isEmpty())
            <span class="chip chip--muted">${messages.get("profile.noPermissions")}</span>
            @else
            @for(String permission : permissions)
            <span class="chip">${permission}</span>
            @endfor
            @endif
        </div>
    </section>
</main>
</body>
</html>
