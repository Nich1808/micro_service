@import kh.edu.istad.platform.iamweb.i18n.Messages
@param Messages messages
@param java.util.List<String> providers
@param boolean error
@param boolean logout
@param boolean registered
@param String path
@param org.springframework.security.web.csrf.CsrfToken csrfToken
<!DOCTYPE html>
<html lang="${messages.language()}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("login.title")} · ${messages.get("app.name")}</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<main class="page centered">
    <div class="meta">
        <div class="brand">
            <span class="brand__mark">D</span>
            <span class="brand__name">${messages.get("app.name")}</span>
        </div>
        <nav class="lang">
            @if(messages.isEnglish())
            <a class="is-active" href="${path}?lang=en">${messages.get("common.english")}</a>
            <a href="${path}?lang=km">${messages.get("common.khmer")}</a>
            @else
            <a href="${path}?lang=en">${messages.get("common.english")}</a>
            <a class="is-active" href="${path}?lang=km">${messages.get("common.khmer")}</a>
            @endif
        </nav>
    </div>

    <section class="card">
        <h1>${messages.get("login.title")}</h1>
        <p class="subtitle">${messages.get("login.subtitle")}</p>

        @if(error)
        <div class="alert alert--error">${messages.get("login.error")}</div>
        @endif

        @if(logout)
        <div class="alert alert--ok">${messages.get("login.logout")}</div>
        @endif

        @if(registered)
        <div class="alert alert--ok">${messages.get("login.registered")}</div>
        @endif

        <form method="post" action="/login">
            <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
            <div class="field">
                <label for="username">${messages.get("login.username")}</label>
                <input id="username" name="username" type="text" autocomplete="username" required autofocus>
            </div>
            <div class="field">
                <label for="password">${messages.get("login.password")}</label>
                <input id="password" name="password" type="password" autocomplete="current-password" required>
            </div>
            <button type="submit">${messages.get("login.submit")}</button>
        </form>

        @if(!providers.isEmpty())
        <div class="divider">${messages.get("login.or")}</div>
        <div class="social">
            @for(String provider : providers)
            <a class="social__button" href="/oauth2/authorization/${provider}">
                @template.partials.providerIcon(provider)
                <span>${messages.get("login.continueWith", messages.providerLabel(provider))}</span>
            </a>
            @endfor
        </div>
        @endif
    </section>

    <p class="subtitle" style="text-align:center;margin-top:20px">
        ${messages.get("login.noAccount")}
        <a href="/register">${messages.get("login.registerLink")}</a>
    </p>
</main>
</body>
</html>
