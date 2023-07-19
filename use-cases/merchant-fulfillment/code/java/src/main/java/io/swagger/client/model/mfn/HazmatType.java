/*
 * Selling Partner API for Merchant Fulfillment
 * The Selling Partner API for Merchant Fulfillment helps you build applications that let sellers purchase shipping for non-Prime and Prime orders using Amazon’s Buy Shipping Services.
 *
 * OpenAPI spec version: v0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model.mfn;

import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Hazardous materials options for a package. Consult the terms and conditions for each carrier for more information on hazardous materials.
 */
@JsonAdapter(HazmatType.Adapter.class)
public enum HazmatType {
  
  NONE("None"),
  
  LQHAZMAT("LQHazmat");

  private String value;

  HazmatType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static HazmatType fromValue(String text) {
    for (HazmatType b : HazmatType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }

  public static class Adapter extends TypeAdapter<HazmatType> {
    @Override
    public void write(final JsonWriter jsonWriter, final HazmatType enumeration) throws IOException {
      jsonWriter.value(enumeration.getValue());
    }

    @Override
    public HazmatType read(final JsonReader jsonReader) throws IOException {
      String value = jsonReader.nextString();
      return HazmatType.fromValue(String.valueOf(value));
    }
  }
}

