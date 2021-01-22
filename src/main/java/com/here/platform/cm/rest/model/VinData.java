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
 * VinData
 */

public class VinData {
  @JsonProperty("approvedCount")
  private Long approvedCount;

  @JsonProperty("pendingCount")
  private Long pendingCount;

  @JsonProperty("revokedCount")
  private Long revokedCount;

  @JsonProperty("vinHash")
  private String vinHash;

  @JsonProperty("vinLabel")
  private String vinLabel;

  public VinData approvedCount(Long approvedCount) {
    this.approvedCount = approvedCount;
    return this;
  }

   /**
   * Count of approved consents for the user&#39;s VIN
   * @return approvedCount
  **/
  @ApiModelProperty(value = "Count of approved consents for the user's VIN")
  public Long getApprovedCount() {
    return approvedCount;
  }

  public void setApprovedCount(Long approvedCount) {
    this.approvedCount = approvedCount;
  }

  public VinData pendingCount(Long pendingCount) {
    this.pendingCount = pendingCount;
    return this;
  }

   /**
   * Count of pending consents for the user&#39;s VIN
   * @return pendingCount
  **/
  @ApiModelProperty(value = "Count of pending consents for the user's VIN")
  public Long getPendingCount() {
    return pendingCount;
  }

  public void setPendingCount(Long pendingCount) {
    this.pendingCount = pendingCount;
  }

  public VinData revokedCount(Long revokedCount) {
    this.revokedCount = revokedCount;
    return this;
  }

   /**
   * Count of revoked consents for the user&#39;s VIN
   * @return revokedCount
  **/
  @ApiModelProperty(value = "Count of revoked consents for the user's VIN")
  public Long getRevokedCount() {
    return revokedCount;
  }

  public void setRevokedCount(Long revokedCount) {
    this.revokedCount = revokedCount;
  }

  public VinData vinHash(String vinHash) {
    this.vinHash = vinHash;
    return this;
  }

   /**
   * Hashed VIN value
   * @return vinHash
  **/
  @ApiModelProperty(value = "Hashed VIN value")
  public String getVinHash() {
    return vinHash;
  }

  public void setVinHash(String vinHash) {
    this.vinHash = vinHash;
  }

  public VinData vinLabel(String vinLabel) {
    this.vinLabel = vinLabel;
    return this;
  }

   /**
   * Last 8 characters of VIN
   * @return vinLabel
  **/
  @ApiModelProperty(value = "Last 8 characters of VIN")
  public String getVinLabel() {
    return vinLabel;
  }

  public void setVinLabel(String vinLabel) {
    this.vinLabel = vinLabel;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VinData vinData = (VinData) o;
    return Objects.equals(this.approvedCount, vinData.approvedCount) &&
        Objects.equals(this.pendingCount, vinData.pendingCount) &&
        Objects.equals(this.revokedCount, vinData.revokedCount) &&
        Objects.equals(this.vinHash, vinData.vinHash) &&
        Objects.equals(this.vinLabel, vinData.vinLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(approvedCount, pendingCount, revokedCount, vinHash, vinLabel);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VinData {\n");
    
    sb.append("    approvedCount: ").append(toIndentedString(approvedCount)).append("\n");
    sb.append("    pendingCount: ").append(toIndentedString(pendingCount)).append("\n");
    sb.append("    revokedCount: ").append(toIndentedString(revokedCount)).append("\n");
    sb.append("    vinHash: ").append(toIndentedString(vinHash)).append("\n");
    sb.append("    vinLabel: ").append(toIndentedString(vinLabel)).append("\n");
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

