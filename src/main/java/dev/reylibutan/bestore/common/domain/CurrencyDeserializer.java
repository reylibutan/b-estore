package dev.reylibutan.bestore.common.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import dev.reylibutan.bestore.common.domain.Currency;

import java.io.IOException;

public class CurrencyDeserializer extends JsonDeserializer<Currency> {

  @Override
  public Currency deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    return Currency.getValue(parser.getText());
  }
}
