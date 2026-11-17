@import kh.edu.istad.platform.iamweb.i18n.Messages
@param Messages messages
@param java.util.List<kh.edu.istad.platform.iamapplicationservice.port.out.AuthorizedApplicationQuery.AuthorizedApplication> applications
@param String revoked
@param org.springframework.security.web.csrf.CsrfToken csrfToken
<!DOCTYPE html>
<html lang="${messages.language()}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("apps.title")} · ${messages.get("app.name")}</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<main class="page">
    <div class="brand">
        <img class="brand__logo" src="/images/logo.png" alt="${messages.get("app.name")}">
    </div>

    <section class="card">
        <h1>${messages.get("apps.title")}</h1>
        <p class="subtitle">${messages.get("apps.subtitle")}</p>

        @if(revoked != null)
        <div class="alert alert--ok">${messages.get("apps.revoked", revoked)}</div>
        @endif

        <ul class="list">
            @for(kh.edu.istad.platform.iamapplicationservice.port.out.AuthorizedApplicationQuery.AuthorizedApplication application : applications)
            <li class="list__item">
                <span>
                    <span class="list__title">${application.clientName()}</span><br>
                    <span class="list__meta">
                        ${messages.get("apps.scopes")}: ${String.join(", ", application.scopes())}
                    </span>
                </span>
                <form method="post" action="/profile/apps/${application.clientId()}/revoke">
                    <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                    <button type="submit" class="danger button--sm">${messages.get("apps.revoke")}</button>
                </form>
            </li>
            @endfor

            @if(applications.isEmpty())
            <li class="list__item">
                <span class="list__meta">${messages.get("apps.none")}</span>
            </li>
            @endif
        </ul>

        <div class="nav">
            <a class="button button--secondary" href="/">${messages.get("common.back")}</a>
        </div>
    </section>
</main>
</body>
</html>
