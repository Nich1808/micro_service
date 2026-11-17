package gg.jte.generated.ondemand;
import kh.edu.istad.platform.iamweb.i18n.Messages;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteconsentGenerated {
	public static final String JTE_NAME = "consent.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,12,12,12,12,12,12,18,18,18,18,18,18,18,18,18,22,22,22,23,23,23,25,25,25,28,28,28,28,28,28,28,28,28,29,29,29,29,29,29,29,29,29,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,32,32,34,34,34,34,34,34,34,34,34,35,35,35,37,37,40,40,40,45,45,45,45,45,45,45,45,45,46,46,46,46,46,46,46,46,46,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,48,48,48,54,0,0,1,2,3,4,5,6,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Messages messages, String clientId, String clientName, String state, java.util.List<String> scopes, org.springframework.security.web.csrf.CsrfToken csrfToken) {
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
		jteOutput.writeUserContent(messages.get("consent.title"));
		jteOutput.writeContent(" · ");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(messages.get("app.name"));
		jteOutput.writeContent("</title>\r\n    <link rel=\"stylesheet\" href=\"/css/app.css\">\r\n</head>\r\n<body>\r\n<main class=\"page centered\">\r\n    <div class=\"brand\">\r\n        <img class=\"brand__logo\" src=\"/images/logo.png\"");
		var __jte_html_attribute_1 = messages.get("app.name");
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
			jteOutput.writeContent(" alt=\"");
			jteOutput.setContext("img", "alt");
			jteOutput.writeUserContent(__jte_html_attribute_1);
			jteOutput.setContext("img", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n    </div>\r\n\r\n    <section class=\"card\">\r\n        <h1>");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(messages.get("consent.title"));
		jteOutput.writeContent("</h1>\r\n        <p class=\"subtitle\">");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(messages.get("consent.subtitle", clientName));
		jteOutput.writeContent("</p>\r\n\r\n        <h2>");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(messages.get("consent.requestedScopes"));
		jteOutput.writeContent("</h2>\r\n\r\n        <form method=\"post\" action=\"/oauth2/authorize\">\r\n            <input type=\"hidden\" name=\"client_id\"");
		var __jte_html_attribute_2 = clientId;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_2)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_2);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <input type=\"hidden\" name=\"state\"");
		var __jte_html_attribute_3 = state;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_3)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_3);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <input type=\"hidden\"");
		var __jte_html_attribute_4 = csrfToken.getParameterName();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_4)) {
			jteOutput.writeContent(" name=\"");
			jteOutput.setContext("input", "name");
			jteOutput.writeUserContent(__jte_html_attribute_4);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		var __jte_html_attribute_5 = csrfToken.getToken();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_5)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_5);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n\r\n            ");
		for (String scope : scopes) {
			jteOutput.writeContent("\r\n            <label class=\"scope\">\r\n                <input type=\"checkbox\" name=\"scope\"");
			var __jte_html_attribute_6 = scope;
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_6)) {
				jteOutput.writeContent(" value=\"");
				jteOutput.setContext("input", "value");
				jteOutput.writeUserContent(__jte_html_attribute_6);
				jteOutput.setContext("input", null);
				jteOutput.writeContent("\"");
			}
			jteOutput.writeContent(" checked>\r\n                <span>");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(messages.scopeLabel(scope));
			jteOutput.writeContent("</span>\r\n            </label>\r\n            ");
		}
		jteOutput.writeContent("\r\n\r\n            <div class=\"actions\" style=\"margin-top:18px\">\r\n                <button type=\"submit\">");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(messages.get("consent.allow"));
		jteOutput.writeContent("</button>\r\n            </div>\r\n        </form>\r\n\r\n        <form method=\"post\" action=\"/oauth2/authorize\" style=\"margin-top:10px\">\r\n            <input type=\"hidden\" name=\"client_id\"");
		var __jte_html_attribute_7 = clientId;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_7)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_7);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <input type=\"hidden\" name=\"state\"");
		var __jte_html_attribute_8 = state;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_8)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_8);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <input type=\"hidden\"");
		var __jte_html_attribute_9 = csrfToken.getParameterName();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_9)) {
			jteOutput.writeContent(" name=\"");
			jteOutput.setContext("input", "name");
			jteOutput.writeUserContent(__jte_html_attribute_9);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		var __jte_html_attribute_10 = csrfToken.getToken();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_10)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_10);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <button type=\"submit\" class=\"secondary\">");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(messages.get("consent.cancel"));
		jteOutput.writeContent("</button>\r\n        </form>\r\n    </section>\r\n</main>\r\n</body>\r\n</html>\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Messages messages = (Messages)params.get("messages");
		String clientId = (String)params.get("clientId");
		String clientName = (String)params.get("clientName");
		String state = (String)params.get("state");
		java.util.List<String> scopes = (java.util.List<String>)params.get("scopes");
		org.springframework.security.web.csrf.CsrfToken csrfToken = (org.springframework.security.web.csrf.CsrfToken)params.get("csrfToken");
		render(jteOutput, jteHtmlInterceptor, messages, clientId, clientName, state, scopes, csrfToken);
	}
}
