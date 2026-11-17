@import kh.edu.istad.platform.iamweb.i18n.Messages
@param Messages messages
@param String clientId
@param String clientName
@param String state
@param java.util.List<String> scopes
@param org.springframework.security.web.csrf.CsrfToken csrfToken
<!DOCTYPE html>
<html lang="${messages.language()}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("consent.title")} · ${messages.get("app.name")}</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<main class="page centered">
    <div class="brand">
        <img class="brand__logo" src="/images/logo.png" alt="${messages.get("app.name")}">
    </div>

    <section class="card">
        <h1>${messages.get("consent.title")}</h1>
        <p class="subtitle">${messages.get("consent.subtitle", clientName)}</p>

        <h2>${messages.get("consent.requestedScopes")}</h2>

        <form method="post" action="/oauth2/authorize">
            <input type="hidden" name="client_id" value="${clientId}">
            <input type="hidden" name="state" value="${state}">
            <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">

            @for(String scope : scopes)
            <label class="scope">
                <input type="checkbox" name="scope" value="${scope}" checked>
                <span>${messages.scopeLabel(scope)}</span>
            </label>
            @endfor

            <div class="actions" style="margin-top:18px">
                <button type="submit">${messages.get("consent.allow")}</button>
            </div>
        </form>

        <form method="post" action="/oauth2/authorize" style="margin-top:10px">
            <input type="hidden" name="client_id" value="${clientId}">
            <input type="hidden" name="state" value="${state}">
            <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
            <button type="submit" class="secondary">${messages.get("consent.cancel")}</button>
        </form>
    </section>
</main>
</body>
</html>
