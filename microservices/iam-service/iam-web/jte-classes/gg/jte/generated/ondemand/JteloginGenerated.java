package gg.jte.generated.ondemand;
import kh.edu.istad.platform.iamweb.i18n.Messages;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteloginGenerated {
	public static final String JTE_NAME = "login.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,0,0,9,9,9,9,9,9,9,9,9,13,13,13,13,13,13,25,25,25,26,26,26,28,28,29,29,29,30,30,32,32,33,33,33,34,34,36,36,37,37,37,38,38,41,41,41,41,41,41,41,41,41,41,41,41,41,41,41,41,41,43,43,43,47,47,47,50,50,50,53,53,54,54,54,56,56,57,57,57,57,58,58,58,58,58,58,58,58,58,59,59,59,59,59,59,59,59,59,60,60,62,62,64,64,67,67,67,68,68,68,74,0,0,1,2,3,4,5,6,7,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Messages messages, java.util.List<String> providers, boolean error, boolean logout, boolean registered, String path, org.springframework.security.web.csrf.CsrfToken csrfToken) {
		jteOutput.writeContent("<!DOCTYPE html>\r\n<html");
		var __jte_html_attribute_0 = messages.language();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
			jteOutput.writeContent(" lang=\"");
			jteOutput.setContext("html", "lang");
			jteOutput.writeUserContent(__jte_html_attribute_0);
			jteOutput.setContext("html", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n<head>\r\n    <meta charset=\"utf-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n    <title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(messages.get("login.title"));
		jteOutput.writeContent(" · ");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(messages.get("app.name"));
		jteOutput.writeContent("</title>\r\n    <link rel=\"stylesheet\" href=\"/css/app.css\">\r\n</head>\r\n<body class=\"auth\">\r\n<div class=\"auth__decor auth__decor--a\"></div>\r\n<div class=\"auth__decor auth__decor--b\"></div>\r\n<div class=\"auth__decor auth__decor--c\"></div>\r\n<div class=\"auth__decor auth__decor--d\"></div>\r\n\r\n<main class=\"auth__shell\">\r\n\r\n    <section class=\"auth__card\">\r\n        <h1 class=\"auth__title\">");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(messages.get("login.title"));
		jteOutput.writeContent("</h1>\r\n        <p class=\"auth__subtitle\">");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(messages.get("login.subtitle"));
		jteOutput.writeContent("</p>\r\n\r\n        ");
		if (error) {
			jteOutput.writeContent("\r\n        <div class=\"alert alert--error\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(messages.get("login.error"));
			jteOutput.writeContent("</div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        ");
		if (logout) {
			jteOutput.writeContent("\r\n        <div class=\"alert alert--ok\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(messages.get("login.logout"));
			jteOutput.writeContent("</div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        ");
		if (registered) {
			jteOutput.writeContent("\r\n        <div class=\"alert alert--ok\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(messages.get("login.registered"));
			jteOutput.writeContent("</div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        <form method=\"post\" action=\"/login\">\r\n            <input type=\"hidden\"");
		var __jte_html_attribute_1 = csrfToken.getParameterName();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
			jteOutput.writeContent(" name=\"");
			jteOutput.setContext("input", "name");
			jteOutput.writeUserContent(__jte_html_attribute_1);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		var __jte_html_attribute_2 = csrfToken.getToken();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_2)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_2);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <div class=\"field field--line\">\r\n                <label for=\"username\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("login.username"));
		jteOutput.writeContent("</label>\r\n                <input id=\"username\" name=\"username\" type=\"text\" autocomplete=\"username\" required autofocus>\r\n            </div>\r\n            <div class=\"field field--line\">\r\n                <label for=\"password\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("login.password"));
		jteOutput.writeContent("</label>\r\n                <input id=\"password\" name=\"password\" type=\"password\" autocomplete=\"current-password\" required>\r\n            </div>\r\n            <button class=\"auth__submit\" type=\"submit\">");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(messages.get("login.submit"));
		jteOutput.writeContent("</button>\r\n        </form>\r\n\r\n        ");
		if (!providers.isEmpty()) {
			jteOutput.writeContent("\r\n        <div class=\"divider divider--label\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(messages.get("login.orContinueWith"));
			jteOutput.writeContent("</div>\r\n        <div class=\"social social--row\">\r\n            ");
			for (String provider : providers) {
				jteOutput.writeContent("\r\n            <a class=\"social__icon\" href=\"/oauth2/authorization/");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent(provider);
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\"\r\n              ");
				var __jte_html_attribute_3 = messages.get("login.continueWith", messages.providerLabel(provider));
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_3)) {
					jteOutput.writeContent(" title=\"");
					jteOutput.setContext("a", "title");
					jteOutput.writeUserContent(__jte_html_attribute_3);
					jteOutput.setContext("a", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n              ");
				var __jte_html_attribute_4 = messages.get("login.continueWith", messages.providerLabel(provider));
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_4)) {
					jteOutput.writeContent(" aria-label=\"");
					jteOutput.setContext("a", "aria-label");
					jteOutput.writeUserContent(__jte_html_attribute_4);
					jteOutput.setContext("a", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent(">\r\n                ");
				gg.jte.generated.ondemand.partials.JteproviderIconGenerated.render(jteOutput, jteHtmlInterceptor, provider);
				jteOutput.writeContent("\r\n            </a>\r\n            ");
			}
			jteOutput.writeContent("\r\n        </div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        <p class=\"auth__footer\">\r\n            ");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(messages.get("login.noAccount"));
		jteOutput.writeContent("\r\n            <a href=\"/register\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("login.registerLink"));
		jteOutput.writeContent("</a>\r\n        </p>\r\n    </section>\r\n</main>\r\n</body>\r\n</html>\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Messages messages = (Messages)params.get("messages");
		java.util.List<String> providers = (java.util.List<String>)params.get("providers");
		boolean error = (boolean)params.get("error");
		boolean logout = (boolean)params.get("logout");
		boolean registered = (boolean)params.get("registered");
		String path = (String)params.get("path");
		org.springframework.security.web.csrf.CsrfToken csrfToken = (org.springframework.security.web.csrf.CsrfToken)params.get("csrfToken");
		render(jteOutput, jteHtmlInterceptor, messages, providers, error, logout, registered, path, csrfToken);
	}
}
