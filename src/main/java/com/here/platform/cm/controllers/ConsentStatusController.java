package com.here.platform.cm.controllers;

import com.here.platform.cm.enums.MPConsumers;
import com.here.platform.cm.rest.model.ConsentInfo.StateEnum;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;
import lombok.Builder;
import lombok.Data;


public class ConsentStatusController extends BaseConsentService {

    private final String consentsBasePath = "/consents";
    private String consumerBearerToken = "";

    public ConsentStatusController withConsumerToken(MPConsumers consumer) {
        this.consumerBearerToken = "Bearer " + consumer.getToken();
        return this;
    }

    @Step
    public Response approveConsent(NewConsent consent, String privateBearerToken) {
        return consentServiceClient(consentsBasePath)
                .header("Authorization", privateBearerToken)
                .body(consent)
                .put("/approve");
    }

    @Step
    public Response revokeConsent(NewConsent consent, String privateBearerToken) {
        return consentServiceClient(consentsBasePath)
                .header("Authorization", privateBearerToken)
                .header("X-Test-Request", "No need to notify")
                .body(consent)
                .put("/revoke");
    }

    @Step
    public Response getConsentStatusByIdAndVin(String consentRequestId, String vin) {
        return consentServiceClient(consentsBasePath)
                .header("Authorization", this.consumerBearerToken)
                .queryParams(Map.of("consentRequestId", consentRequestId, "vin", vin))
                .get("/status");
    }

    @Step
    public Response getConsentsInfo(String vin, PageableConsent pageableConsent) {
        return consentServiceClient(consentsBasePath)
                .queryParams(pageableConsent.toMap())
                .get("/{vin}/info", vin);
    }

    public Response getConsentRequestInfoByVinAndCrid(String vin, String crid, String privateBearerToken) {
        return consentServiceClient(consentsBasePath)
                .header("Authorization", privateBearerToken)
                .get("/{vin}/consentRequests/{crid}/info", vin, crid);
    }


    @Data
    @Builder
    public static class NewConsent {

        public String authorizationCode, vinHash, consentRequestId;

    }

    @Data
    @Builder
    public static class PageableConsent {

        @Builder.Default
        private Integer page = 0, pageSize = 10000;
        private StateEnum stateEnum;

        public Map<String, String> toMap() {
            return Map.of("page", page.toString(), "pageSize", pageSize.toString(), "state", stateEnum.getValue());
        }

    }

}
