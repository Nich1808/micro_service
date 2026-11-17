@import kh.edu.istad.platform.iamweb.i18n.Messages
@param Messages messages
@param boolean changed
@param String error
@param org.springframework.security.web.csrf.CsrfToken csrfToken
<!DOCTYPE html>
<html lang="${messages.language()}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("password.title")} · ${messages.get("app.name")}</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<main class="page centered">
    <div class="brand">
        <span class="brand__mark">D</span>
        <span class="brand__name">${messages.get("app.name")}</span>
    </div>

    <section class="card">
        <h1>${messages.get("password.title")}</h1>
        <p class="subtitle">${messages.get("password.subtitle")}</p>

        @if(changed)
        <div class="alert alert--ok">${messages.get("password.changed")}</div>
        @endif

        @if(error != null)
        <div class="alert alert--error">${error}</div>
        @endif

        <form method="post" action="/profile/password">
            <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">

            <div class="field">
                <label for="currentPassword">${messages.get("password.current")}</label>
                <input id="currentPassword" name="currentPassword" type="password" autocomplete="current-password" required>
            </div>
            <div class="field">
                <label for="newPassword">${messages.get("password.new")}</label>
                <input id="newPassword" name="newPassword" type="password" minlength="8" autocomplete="new-password" required>
            </div>
            <div class="field">
                <label for="confirmPassword">${messages.get("password.confirm")}</label>
                <input id="confirmPassword" name="confirmPassword" type="password" minlength="8" autocomplete="new-password" required>
            </div>

            <div class="actions">
                <button type="submit">${messages.get("password.submit")}</button>
                <a class="button button--secondary" href="/">${messages.get("profileEdit.cancel")}</a>
            </div>
        </form>
    </section>
</main>
</body>
</html>
