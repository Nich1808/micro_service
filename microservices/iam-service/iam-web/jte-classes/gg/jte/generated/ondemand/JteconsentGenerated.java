package gg.jte.generated.ondemand;
import kh.edu.istad.platform.iamweb.i18n.Messages;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteconsentGenerated {
	public static final String JTE_NAME = "consent.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,12,12,12,12,12,12,19,19,19,23,23,23,24,24,24,26,26,26,29,29,29,29,29,29,29,29,29,30,30,30,30,30,30,30,30,30,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,33,33,35,35,35,35,35,35,35,35,35,36,36,36,38,38,41,41,41,46,46,46,46,46,46,46,46,46,47,47,47,47,47,47,47,47,47,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,49,49,49,55,0,0,1,2,3,4,5,6,0,0,0};
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
		jteOutput.writeContent("</title>\r\n    <link rel=\"stylesheet\" href=\"/css/app.css\">\r\n</head>\r\n<body>\r\n<main class=\"page centered\">\r\n    <div class=\"brand\">\r\n        <span class=\"brand__mark\">D</span>\r\n        <span class=\"brand__name\">");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(messages.get("app.name"));
		jteOutput.writeContent("</span>\r\n    </div>\r\n\r\n    <section class=\"card\">\r\n        <h1>");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(messages.get("consent.title"));
		jteOutput.writeContent("</h1>\r\n        <p class=\"subtitle\">");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(messages.get("consent.subtitle", clientName));
		jteOutput.writeContent("</p>\r\n\r\n        <h2>");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(messages.get("consent.requestedScopes"));
		jteOutput.writeContent("</h2>\r\n\r\n        <form method=\"post\" action=\"/oauth2/authorize\">\r\n            <input type=\"hidden\" name=\"client_id\"");
		var __jte_html_attribute_1 = clientId;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_1);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <input type=\"hidden\" name=\"state\"");
		var __jte_html_attribute_2 = state;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_2)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_2);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <input type=\"hidden\"");
		var __jte_html_attribute_3 = csrfToken.getParameterName();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_3)) {
			jteOutput.writeContent(" name=\"");
			jteOutput.setContext("input", "name");
			jteOutput.writeUserContent(__jte_html_attribute_3);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		var __jte_html_attribute_4 = csrfToken.getToken();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_4)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_4);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n\r\n            ");
		for (String scope : scopes) {
			jteOutput.writeContent("\r\n            <label class=\"scope\">\r\n                <input type=\"checkbox\" name=\"scope\"");
			var __jte_html_attribute_5 = scope;
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_5)) {
				jteOutput.writeContent(" value=\"");
				jteOutput.setContext("input", "value");
				jteOutput.writeUserContent(__jte_html_attribute_5);
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
		var __jte_html_attribute_6 = clientId;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_6)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_6);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <input type=\"hidden\" name=\"state\"");
		var __jte_html_attribute_7 = state;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_7)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_7);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            <input type=\"hidden\"");
		var __jte_html_attribute_8 = csrfToken.getParameterName();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_8)) {
			jteOutput.writeContent(" name=\"");
			jteOutput.setContext("input", "name");
			jteOutput.writeUserContent(__jte_html_attribute_8);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		var __jte_html_attribute_9 = csrfToken.getToken();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_9)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_9);
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
