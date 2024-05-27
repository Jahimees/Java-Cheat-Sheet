package lu.labo.document.utils.xss;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lu.labo.xss.XssProtector;
import java.io.IOException;

public class ObjectMapperUtils {
  public static ObjectMapper createMapper() {
    return configure(new ObjectMapper());
  }

  private static ObjectMapper configure(ObjectMapper mapper) {
    SimpleModule module = new SimpleModule();
    module.addSerializer(String.class, new XssJacksonSerializer());
    module.addDeserializer(String.class, new XssJacksonDeserializer());

    return mapper
            .registerModule(module)
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setDateFormat(new StdDateFormat());
  }

  public static class XssJacksonDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
      return cleanXss(jsonParser.getText());
    }
  }

  public static class XssJacksonSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
      jsonGenerator.writeString(s);
      //jsonGenerator.writeString(Jsoup.clean(s, ALLOWED_TAGS));
      //jsonGenerator.writeString(s.contains("<") ? Jsoup.clean(s, ALLOWED_TAGS) : s);
    }
  }

  public static String cleanXss(String text) {
    return XssProtector.cleanPlainText(text);
  }
}
