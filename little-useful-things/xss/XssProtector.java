package lu.labo.xss;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Safelist;

import java.text.Normalizer;

public class XssProtector {

  private static final Safelist ALLOWED_TAGS;
  private static final Document.OutputSettings OUTPUT_SETTINGS;

  static {
    ALLOWED_TAGS = Safelist.none();
    ALLOWED_TAGS.addTags("a", "b", "blockquote", "cite", "dd", "dl", "dt", "em",
      "i", "li", "ol", "p", "q", "small", "span", "strike", "strong", "sub",
      "sup", "u", "pre", "br", "code", "ul", "div");
    ALLOWED_TAGS.addAttributes("a", "href");
    ALLOWED_TAGS.addProtocols("a", "href", "http", "https");
    OUTPUT_SETTINGS = new Document.OutputSettings();
    OUTPUT_SETTINGS.prettyPrint(false)
      .charset("utf8")
      .escapeMode(Entities.EscapeMode.xhtml)
      .outline(false)
      .syntax(Document.OutputSettings.Syntax.html);
  }

  public static String cleanPlainText(final String text) {
    String out = text;
    if (out.contains("<")) {
      Document doc = Jsoup.parseBodyFragment(out);
      out = doc.body().wholeText();
    }
    return Normalizer.normalize(out, Normalizer.Form.NFD);
  }

  public static String cleanBasicHtml(final String html) {
    String out = html;
    if (out.contains("<")) {
      out = Jsoup.clean(out, "", ALLOWED_TAGS, OUTPUT_SETTINGS);
    }
    return Normalizer.normalize(out, Normalizer.Form.NFD);
  }

}
