@import kh.edu.istad.platform.iamweb.i18n.Messages
@param Messages messages
@param kh.edu.istad.platform.iamweb.web.ProfileController.ProfileForm form
@param boolean saved
@param String error
@param org.springframework.security.web.csrf.CsrfToken csrfToken
<!DOCTYPE html>
<html lang="${messages.language()}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("profileEdit.title")} · ${messages.get("app.name")}</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<main class="page">
    <div class="brand">
        <img class="brand__logo" src="/images/logo.png" alt="${messages.get("app.name")}">
    </div>

    <section class="card">
        <h1>${messages.get("profileEdit.title")}</h1>
        <p class="subtitle">${messages.get("profileEdit.subtitle")}</p>

        @if(saved)
        <div class="alert alert--ok">${messages.get("profileEdit.saved")}</div>
        @endif

        @if(error != null)
        <div class="alert alert--error">${error}</div>
        @endif

        <form method="post" action="/profile/edit">
            <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">

            <div class="grid">
                <div class="field">
                    <label for="givenName">${messages.get("profileEdit.givenName")}</label>
                    <input id="givenName" name="givenName" type="text" value="${form.givenName()}">
                </div>
                <div class="field">
                    <label for="familyName">${messages.get("profileEdit.familyName")}</label>
                    <input id="familyName" name="familyName" type="text" value="${form.familyName()}">
                </div>
            </div>

            <div class="grid">
                <div class="field">
                    <label for="middleName">${messages.get("profileEdit.middleName")}</label>
                    <input id="middleName" name="middleName" type="text" value="${form.middleName()}">
                </div>
                <div class="field">
                    <label for="nickname">${messages.get("profileEdit.nickname")}</label>
                    <input id="nickname" name="nickname" type="text" value="${form.nickname()}">
                </div>
            </div>

            <div class="grid">
                <div class="field">
                    <label for="gender">${messages.get("profileEdit.gender")}</label>
                    <select id="gender" name="gender">
                        <option value="female"$unsafe{"female".equals(form.gender()) ? " selected" : ""}>${messages.get("gender.female")}</option>
                        <option value="male"$unsafe{"male".equals(form.gender()) ? " selected" : ""}>${messages.get("gender.male")}</option>
                        <option value="other"$unsafe{"other".equals(form.gender()) ? " selected" : ""}>${messages.get("gender.other")}</option>
                    </select>
                </div>
                <div class="field">
                    <label for="birthdate">${messages.get("profileEdit.birthdate")}</label>
                    <input id="birthdate" name="birthdate" type="date" value="${form.birthdate() == null ? "" : form.birthdate().toString()}">
                </div>
            </div>

            <div class="grid">
                <div class="field">
                    <label for="phoneNumber">${messages.get("profileEdit.phoneNumber")}</label>
                    <input id="phoneNumber" name="phoneNumber" type="tel" value="${form.phoneNumber()}">
                </div>
                <div class="field">
                    <label for="websiteUrl">${messages.get("profileEdit.websiteUrl")}</label>
                    <input id="websiteUrl" name="websiteUrl" type="url" value="${form.websiteUrl()}">
                </div>
            </div>

            <div class="grid">
                <div class="field">
                    <label for="pictureUrl">${messages.get("profileEdit.pictureUrl")}</label>
                    <input id="pictureUrl" name="pictureUrl" type="url" value="${form.pictureUrl()}">
                </div>
                <div class="field">
                    <label for="zoneinfo">${messages.get("profileEdit.zoneinfo")}</label>
                    <input id="zoneinfo" name="zoneinfo" type="text" value="${form.zoneinfo()}">
                </div>
            </div>

            <div class="field">
                <label for="locale">${messages.get("profileEdit.locale")}</label>
                <select id="locale" name="locale">
                    <option value="en"$unsafe{"km".equals(form.locale()) ? "" : " selected"}>${messages.get("common.english")}</option>
                    <option value="km"$unsafe{"km".equals(form.locale()) ? " selected" : ""}>${messages.get("common.khmer")}</option>
                </select>
            </div>

            <div class="field">
                <label for="streetAddress">${messages.get("profileEdit.streetAddress")}</label>
                <input id="streetAddress" name="streetAddress" type="text" value="${form.streetAddress()}">
            </div>

            <div class="grid">
                <div class="field">
                    <label for="locality">${messages.get("profileEdit.locality")}</label>
                    <input id="locality" name="locality" type="text" value="${form.locality()}">
                </div>
                <div class="field">
                    <label for="region">${messages.get("profileEdit.region")}</label>
                    <input id="region" name="region" type="text" value="${form.region()}">
                </div>
            </div>

            <div class="grid">
                <div class="field">
                    <label for="postalCode">${messages.get("profileEdit.postalCode")}</label>
                    <input id="postalCode" name="postalCode" type="text" value="${form.postalCode()}">
                </div>
                <div class="field">
                    <label for="country">${messages.get("profileEdit.country")}</label>
                    <input id="country" name="country" type="text" maxlength="2" value="${form.country()}">
                </div>
            </div>

            <div class="actions">
                <button type="submit">${messages.get("profileEdit.submit")}</button>
                <a class="button button--secondary" href="/">${messages.get("profileEdit.cancel")}</a>
            </div>
        </form>
    </section>
</main>
</body>
</html>
