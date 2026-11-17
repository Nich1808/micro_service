@import kh.edu.istad.platform.iamweb.i18n.Messages
@param Messages messages
@param kh.edu.istad.platform.iamweb.web.RegisterController.RegisterForm form
@param java.util.Map<String, String> errors
@param String path
@param org.springframework.security.web.csrf.CsrfToken csrfToken
<!DOCTYPE html>
<html lang="${messages.language()}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("register.title")} · ${messages.get("app.name")}</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body class="auth">
<div class="auth__decor auth__decor--a"></div>
<div class="auth__decor auth__decor--b"></div>
<div class="auth__decor auth__decor--c"></div>
<div class="auth__decor auth__decor--d"></div>

<main class="auth__shell">

    <section class="auth__card">
        <h1 class="auth__title">${messages.get("register.title")}</h1>
        <p class="auth__subtitle">${messages.get("register.subtitle")}</p>

        @if(errors.containsKey("username"))
        <div class="alert alert--error">${errors.get("username")}</div>
        @endif

        <form method="post" action="/register">
            <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
            <div class="grid">
                <div class="field field--line">
                    <label for="username">${messages.get("register.username")}</label>
                    <input id="username" name="username" type="text" value="${form.username()}" required>
                </div>
                <div class="field field--line">
                    <label for="email">${messages.get("register.email")}</label>
                    <input id="email" name="email" type="email" value="${form.email()}" required>
                    @if(errors.containsKey("email"))
                    <div class="field__error">${errors.get("email")}</div>
                    @endif
                </div>
            </div>

            <div class="grid">
                <div class="field field--line">
                    <label for="givenName">${messages.get("register.givenName")}</label>
                    <input id="givenName" name="givenName" type="text" value="${form.givenName()}" required>
                    @if(errors.containsKey("givenName"))
                    <div class="field__error">${errors.get("givenName")}</div>
                    @endif
                </div>
                <div class="field field--line">
                    <label for="familyName">${messages.get("register.familyName")}</label>
                    <input id="familyName" name="familyName" type="text" value="${form.familyName()}" required>
                    @if(errors.containsKey("familyName"))
                    <div class="field__error">${errors.get("familyName")}</div>
                    @endif
                </div>
            </div>

            <div class="grid">
                <div class="field field--line">
                    <label for="password">${messages.get("register.password")}</label>
                    <input id="password" name="password" type="password" minlength="8" required>
                    @if(errors.containsKey("password"))
                    <div class="field__error">${errors.get("password")}</div>
                    @endif
                </div>
                <div class="field field--line">
                    <label for="confirmPassword">${messages.get("register.confirmPassword")}</label>
                    <input id="confirmPassword" name="confirmPassword" type="password" minlength="8" required>
                    @if(errors.containsKey("confirmPassword"))
                    <div class="field__error">${errors.get("confirmPassword")}</div>
                    @endif
                </div>
            </div>

            <div class="field field--line">
                <label for="locale">${messages.get("register.locale")}</label>
                <select id="locale" name="locale">
                    @if("km".equals(form.locale()))
                    <option value="en">${messages.get("common.english")}</option>
                    <option value="km" selected>${messages.get("common.khmer")}</option>
                    @else
                    <option value="en" selected>${messages.get("common.english")}</option>
                    <option value="km">${messages.get("common.khmer")}</option>
                    @endif
                </select>
            </div>

            <div class="auth__actions">
                <button class="auth__submit" type="submit">${messages.get("register.submit")}</button>
                <a class="auth__cancel" href="/login">${messages.get("profileEdit.cancel")}</a>
            </div>
        </form>

        <p class="auth__footer">
            ${messages.get("register.haveAccount")}
            <a href="/login">${messages.get("register.loginLink")}</a>
        </p>
    </section>
</main>
</body>
</html>
