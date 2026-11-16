package gg.jte.generated.ondemand;
import kh.edu.istad.platform.iamweb.i18n.Messages;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteloginGenerated {
	public static final String JTE_NAME = "login.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,0,0,9,9,9,9,9,9,9,9,9,13,13,13,13,13,13,21,21,21,24,24,25,25,25,25,25,25,25,26,26,26,26,26,26,26,27,27,28,28,28,28,28,28,28,29,29,29,29,29,29,29,30,30,35,35,35,36,36,36,38,38,39,39,39,40,40,42,42,43,43,43,44,44,46,46,47,47,47,48,48,51,51,51,51,51,51,51,51,51,51,51,51,51,51,51,51,51,53,53,53,57,57,57,60,60,60,63,63,64,64,64,66,66,67,67,67,67,67,67,67,68,68,70,70,74,74,74,75,75,75,80,0,0,1,2,3,4,5,6,7,0,0,0};
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
		jteOutput.writeContent("</title>\r\n    <link rel=\"stylesheet\" href=\"/css/app.css\">\r\n</head>\r\n<body>\r\n<main class=\"page centered\">\r\n    <div class=\"meta\">\r\n        <div class=\"brand\">\r\n            <span class=\"brand__mark\">IS</span>\r\n            <span class=\"brand__name\">");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(messages.get("app.name"));
		jteOutput.writeContent("</span>\r\n        </div>\r\n        <nav class=\"lang\">\r\n            ");
		if (messages.isEnglish()) {
			jteOutput.writeContent("\r\n            <a class=\"is-active\" href=\"");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(path);
			jteOutput.setContext("a", null);
			jteOutput.writeContent("?lang=en\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(messages.get("common.english"));
			jteOutput.writeContent("</a>\r\n            <a href=\"");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(path);
			jteOutput.setContext("a", null);
			jteOutput.writeContent("?lang=km\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(messages.get("common.khmer"));
			jteOutput.writeContent("</a>\r\n            ");
		} else {
			jteOutput.writeContent("\r\n            <a href=\"");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(path);
			jteOutput.setContext("a", null);
			jteOutput.writeContent("?lang=en\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(messages.get("common.english"));
			jteOutput.writeContent("</a>\r\n            <a class=\"is-active\" href=\"");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(path);
			jteOutput.setContext("a", null);
			jteOutput.writeContent("?lang=km\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(messages.get("common.khmer"));
			jteOutput.writeContent("</a>\r\n            ");
		}
		jteOutput.writeContent("\r\n        </nav>\r\n    </div>\r\n\r\n    <section class=\"card\">\r\n        <h1>");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(messages.get("login.title"));
		jteOutput.writeContent("</h1>\r\n        <p class=\"subtitle\">");
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
		jteOutput.writeContent(">\r\n            <div class=\"field\">\r\n                <label for=\"username\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("login.username"));
		jteOutput.writeContent("</label>\r\n                <input id=\"username\" name=\"username\" type=\"text\" autocomplete=\"username\" required autofocus>\r\n            </div>\r\n            <div class=\"field\">\r\n                <label for=\"password\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("login.password"));
		jteOutput.writeContent("</label>\r\n                <input id=\"password\" name=\"password\" type=\"password\" autocomplete=\"current-password\" required>\r\n            </div>\r\n            <button type=\"submit\">");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(messages.get("login.submit"));
		jteOutput.writeContent("</button>\r\n        </form>\r\n\r\n        ");
		if (!providers.isEmpty()) {
			jteOutput.writeContent("\r\n        <div class=\"divider\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(messages.get("login.or"));
			jteOutput.writeContent("</div>\r\n        <div class=\"social\">\r\n            ");
			for (String provider : providers) {
				jteOutput.writeContent("\r\n            <a class=\"social__button\" href=\"/oauth2/authorization/");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent(provider);
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\">");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(messages.get("login.continueWith", provider));
				jteOutput.writeContent("</a>\r\n            ");
			}
			jteOutput.writeContent("\r\n        </div>\r\n        ");
		}
		jteOutput.writeContent("\r\n    </section>\r\n\r\n    <p class=\"subtitle\" style=\"text-align:center;margin-top:20px\">\r\n        ");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(messages.get("login.noAccount"));
		jteOutput.writeContent("\r\n        <a href=\"/register\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("login.registerLink"));
		jteOutput.writeContent("</a>\r\n    </p>\r\n</main>\r\n</body>\r\n</html>\r\n");
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
