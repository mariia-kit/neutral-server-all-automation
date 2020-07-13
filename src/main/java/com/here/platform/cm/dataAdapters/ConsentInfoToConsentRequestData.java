package com.here.platform.cm.dataAdapters;

import com.here.platform.cm.rest.model.ConsentInfo;
import com.here.platform.cm.rest.model.ConsentRequestData;


public class ConsentInfoToConsentRequestData {

    private final ConsentRequestData consentRequestData;

    public ConsentInfoToConsentRequestData(ConsentInfo consentInfo, String providerId, String consumerId) {
        this.consentRequestData = new ConsentRequestData()
                .providerId(providerId)
                .title(consentInfo.getTitle())
                .containerName(consentInfo.getContainerName())
                .purpose(consentInfo.getPurpose())
                .consumerId(consumerId);
    }

    public ConsentRequestData consentRequestData() {
        return consentRequestData;
    }

}
