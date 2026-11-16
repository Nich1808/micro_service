package gg.jte.generated.ondemand.profile;
import kh.edu.istad.platform.iamweb.i18n.Messages;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JtepasswordGenerated {
	public static final String JTE_NAME = "profile/password.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,0,0,6,6,6,6,6,6,6,6,6,10,10,10,10,10,10,17,17,17,21,21,21,22,22,22,24,24,25,25,25,26,26,28,28,29,29,29,30,30,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,36,36,36,40,40,40,44,44,44,49,49,49,50,50,50,57,0,0,1,2,3,4,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Messages messages, boolean changed, String error, org.springframework.security.web.csrf.CsrfToken csrfToken) {
		jteOutput.writeContent("<!DOCTYPE html>\n<html");
		var __jte_html_attribute_0 = messages.language();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
			jteOutput.writeContent(" lang=\"");
			jteOutput.setContext("html", "lang");
			jteOutput.writeUserContent(__jte_html_attribute_0);
			jteOutput.setContext("html", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\n<head>\n    <meta charset=\"utf-8\">\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n    <title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(messages.get("password.title"));
		jteOutput.writeContent(" · ");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(messages.get("app.name"));
		jteOutput.writeContent("</title>\n    <link rel=\"stylesheet\" href=\"/css/app.css\">\n</head>\n<body>\n<main class=\"page centered\">\n    <div class=\"brand\">\n        <span class=\"brand__mark\">IS</span>\n        <span class=\"brand__name\">");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(messages.get("app.name"));
		jteOutput.writeContent("</span>\n    </div>\n\n    <section class=\"card\">\n        <h1>");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(messages.get("password.title"));
		jteOutput.writeContent("</h1>\n        <p class=\"subtitle\">");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(messages.get("password.subtitle"));
		jteOutput.writeContent("</p>\n\n        ");
		if (changed) {
			jteOutput.writeContent("\n        <div class=\"alert alert--ok\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(messages.get("password.changed"));
			jteOutput.writeContent("</div>\n        ");
		}
		jteOutput.writeContent("\n\n        ");
		if (error != null) {
			jteOutput.writeContent("\n        <div class=\"alert alert--error\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(error);
			jteOutput.writeContent("</div>\n        ");
		}
		jteOutput.writeContent("\n\n        <form method=\"post\" action=\"/profile/password\">\n            <input type=\"hidden\"");
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
		jteOutput.writeContent(">\n\n            <div class=\"field\">\n                <label for=\"currentPassword\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("password.current"));
		jteOutput.writeContent("</label>\n                <input id=\"currentPassword\" name=\"currentPassword\" type=\"password\" autocomplete=\"current-password\" required>\n            </div>\n            <div class=\"field\">\n                <label for=\"newPassword\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("password.new"));
		jteOutput.writeContent("</label>\n                <input id=\"newPassword\" name=\"newPassword\" type=\"password\" minlength=\"8\" autocomplete=\"new-password\" required>\n            </div>\n            <div class=\"field\">\n                <label for=\"confirmPassword\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("password.confirm"));
		jteOutput.writeContent("</label>\n                <input id=\"confirmPassword\" name=\"confirmPassword\" type=\"password\" minlength=\"8\" autocomplete=\"new-password\" required>\n            </div>\n\n            <div class=\"actions\">\n                <button type=\"submit\">");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(messages.get("password.submit"));
		jteOutput.writeContent("</button>\n                <a class=\"button button--secondary\" href=\"/\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("profileEdit.cancel"));
		jteOutput.writeContent("</a>\n            </div>\n        </form>\n    </section>\n</main>\n</body>\n</html>\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Messages messages = (Messages)params.get("messages");
		boolean changed = (boolean)params.get("changed");
		String error = (String)params.get("error");
		org.springframework.security.web.csrf.CsrfToken csrfToken = (org.springframework.security.web.csrf.CsrfToken)params.get("csrfToken");
		render(jteOutput, jteHtmlInterceptor, messages, changed, error, csrfToken);
	}
}
