/*
 * HERE Consent Management API v1
 * HERE Consent Management REST API. More details can be found here: https://confluence.in.here.com/display/OLP/Neutral+Server+Consent+Management
 *
 * OpenAPI spec version: 3.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.here.platform.cm.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;


/**
 * AdditionalLink
 */

public class AdditionalLink {
  @JsonProperty("title")
  private String title;

  @JsonProperty("url")
  private String url;

  public AdditionalLink title(String title) {
    this.title = title;
    return this;
  }

   /**
   * Additional link title
   * @return title
  **/
  @ApiModelProperty(value = "Additional link title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public AdditionalLink url(String url) {
    this.url = url;
    return this;
  }

   /**
   * Http address of resource this link points to
   * @return url
  **/
  @ApiModelProperty(value = "Http address of resource this link points to")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdditionalLink additionalLink = (AdditionalLink) o;
    return Objects.equals(this.title, additionalLink.title) &&
        Objects.equals(this.url, additionalLink.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, url);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdditionalLink {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

