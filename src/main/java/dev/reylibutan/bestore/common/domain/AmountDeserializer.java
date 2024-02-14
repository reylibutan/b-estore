package dev.reylibutan.bestore.common.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class AmountDeserializer extends JsonDeserializer<Amount> {

  @Override
  public Amount deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    return new Amount(parser.getText());
  }
}
