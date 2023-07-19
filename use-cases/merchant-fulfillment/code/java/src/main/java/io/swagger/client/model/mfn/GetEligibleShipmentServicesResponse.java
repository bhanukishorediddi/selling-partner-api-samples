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

import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Response schema.
 */
@ApiModel(description = "Response schema.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-06-06T15:03:26.392+02:00")
public class GetEligibleShipmentServicesResponse {
  @SerializedName("payload")
  private GetEligibleShipmentServicesResult payload = null;

  @SerializedName("errors")
  private ErrorList errors = null;

  public GetEligibleShipmentServicesResponse payload(GetEligibleShipmentServicesResult payload) {
    this.payload = payload;
    return this;
  }

   /**
   * Get payload
   * @return payload
  **/
  @ApiModelProperty(value = "")
  public GetEligibleShipmentServicesResult getPayload() {
    return payload;
  }

  public void setPayload(GetEligibleShipmentServicesResult payload) {
    this.payload = payload;
  }

  public GetEligibleShipmentServicesResponse errors(ErrorList errors) {
    this.errors = errors;
    return this;
  }

   /**
   * One or more unexpected errors occurred during this operation.
   * @return errors
  **/
  @ApiModelProperty(value = "One or more unexpected errors occurred during this operation.")
  public ErrorList getErrors() {
    return errors;
  }

  public void setErrors(ErrorList errors) {
    this.errors = errors;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetEligibleShipmentServicesResponse getEligibleShipmentServicesResponse = (GetEligibleShipmentServicesResponse) o;
    return Objects.equals(this.payload, getEligibleShipmentServicesResponse.payload) &&
        Objects.equals(this.errors, getEligibleShipmentServicesResponse.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(payload, errors);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetEligibleShipmentServicesResponse {\n");
    
    sb.append("    payload: ").append(toIndentedString(payload)).append("\n");
    sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

