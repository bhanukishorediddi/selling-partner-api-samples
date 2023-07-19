/*
 * Selling Partner API for Orders
 * The Selling Partner API for Orders helps you programmatically retrieve order information. These APIs let you develop fast, flexible, custom applications in areas like order synchronization, order research, and demand-based decision support tools. The Orders API only supports orders that are less than two years old. Orders more than two years old will not show in the API response.
 *
 * OpenAPI spec version: v0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model.orders;

import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * The verification status of the order.
 */
@JsonAdapter(VerificationStatus.Adapter.class)
public enum VerificationStatus {
  
  PENDING("Pending"),
  
  APPROVED("Approved"),
  
  REJECTED("Rejected"),
  
  EXPIRED("Expired"),
  
  CANCELLED("Cancelled");

  private String value;

  VerificationStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static VerificationStatus fromValue(String text) {
    for (VerificationStatus b : VerificationStatus.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }

  public static class Adapter extends TypeAdapter<VerificationStatus> {
    @Override
    public void write(final JsonWriter jsonWriter, final VerificationStatus enumeration) throws IOException {
      jsonWriter.value(enumeration.getValue());
    }

    @Override
    public VerificationStatus read(final JsonReader jsonReader) throws IOException {
      String value = jsonReader.nextString();
      return VerificationStatus.fromValue(String.valueOf(value));
    }
  }
}

