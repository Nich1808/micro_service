@import kh.edu.istad.platform.iamweb.i18n.Messages
@param Messages messages
@param java.util.List<kh.edu.istad.platform.iamapplicationservice.result.identity.IdentityResult> identities
@param java.util.List<String> providers
@param boolean hasPassword
@param String linked
@param boolean unlinked
@param String error
@param org.springframework.security.web.csrf.CsrfToken csrfToken
<!DOCTYPE html>
<html lang="${messages.language()}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("accounts.title")} · ${messages.get("app.name")}</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<main class="page">
    <div class="brand">
        <span class="brand__mark">IS</span>
        <span class="brand__name">${messages.get("app.name")}</span>
    </div>

    <section class="card">
        <h1>${messages.get("accounts.title")}</h1>
        <p class="subtitle">${messages.get("accounts.subtitle")}</p>

        @if(linked != null)
        <div class="alert alert--ok">${messages.get("accounts.linked", linked)}</div>
        @endif

        @if(unlinked)
        <div class="alert alert--ok">${messages.get("accounts.unlinked")}</div>
        @endif

        @if(error != null)
        <div class="alert alert--error">${messages.get("accounts.error")}</div>
        @endif

        <ul class="list">
            @if(hasPassword)
            <li class="list__item">
                <span>
                    <span class="list__title">${messages.get("accounts.passwordMethod")}</span><br>
                    <span class="list__meta">${messages.get("profile.username")}</span>
                </span>
            </li>
            @endif

            @for(kh.edu.istad.platform.iamapplicationservice.result.identity.IdentityResult identity : identities)
            <li class="list__item">
                <span>
                    <span class="list__title">${identity.provider()}</span><br>
                    <span class="list__meta">${identity.email() == null ? identity.displayName() : identity.email()}</span>
                </span>
                <form method="post" action="/profile/accounts/${identity.uuid().toString()}/unlink">
                    <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                    <button type="submit" class="danger button--sm">${messages.get("accounts.unlink")}</button>
                </form>
            </li>
            @endfor

            @if(identities.isEmpty())
            <li class="list__item">
                <span class="list__meta">${messages.get("accounts.none")}</span>
            </li>
            @endif
        </ul>

        @if(!providers.isEmpty())
        <h2 style="margin-top:24px">${messages.get("accounts.link")}</h2>
        <div class="social">
            @for(String provider : providers)
            <a class="social__button" href="/profile/accounts/link/${provider}">${messages.get("login.continueWith", provider)}</a>
            @endfor
        </div>
        @endif

        <div class="nav">
            <a class="button button--secondary" href="/">${messages.get("common.back")}</a>
        </div>
    </section>
</main>
</body>
</html>
