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
<body class="auth">
<div class="auth__decor auth__decor--a"></div>
<div class="auth__decor auth__decor--b"></div>
<div class="auth__decor auth__decor--c"></div>
<div class="auth__decor auth__decor--d"></div>

<main class="auth__shell">

    <section class="auth__card">
        <h1 class="auth__title">${messages.get("login.title")}</h1>
        <p class="auth__subtitle">${messages.get("login.subtitle")}</p>

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
            <div class="field field--line">
                <label for="username">${messages.get("login.username")}</label>
                <input id="username" name="username" type="text" autocomplete="username" required autofocus>
            </div>
            <div class="field field--line">
                <label for="password">${messages.get("login.password")}</label>
                <input id="password" name="password" type="password" autocomplete="current-password" required>
            </div>
            <button class="auth__submit" type="submit">${messages.get("login.submit")}</button>
        </form>

        @if(!providers.isEmpty())
        <div class="divider divider--label">${messages.get("login.orContinueWith")}</div>
        <div class="social social--row">
            @for(String provider : providers)
            <a class="social__icon" href="/oauth2/authorization/${provider}"
               title="${messages.get("login.continueWith", messages.providerLabel(provider))}"
               aria-label="${messages.get("login.continueWith", messages.providerLabel(provider))}">
                @template.partials.providerIcon(provider)
            </a>
            @endfor
        </div>
        @endif

        <p class="auth__footer">
            ${messages.get("login.noAccount")}
            <a href="/register">${messages.get("login.registerLink")}</a>
        </p>
    </section>
</main>
</body>
</html>
