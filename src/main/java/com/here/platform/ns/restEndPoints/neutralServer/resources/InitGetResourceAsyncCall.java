package com.here.platform.ns.restEndPoints.neutralServer.resources;

import static com.here.platform.ns.dto.Users.CONSUMER;
import static com.here.platform.ns.dto.Users.PROVIDER;

import com.here.platform.ns.dto.CallsUrl;
import com.here.platform.ns.helpers.resthelper.RestHelper;
import com.here.platform.ns.restEndPoints.BaseRestControllerNS;
import com.here.platform.ns.utils.NS_Config;
import io.restassured.http.Header;
import io.restassured.response.Response;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;


public class InitGetResourceAsyncCall extends
        BaseRestControllerNS<InitGetResourceAsyncCall> {

    public InitGetResourceAsyncCall(String providerName, String vehicleId,
            String resourceName) {
        callMessage = String
                .format("Get [%s] Resource values for vehicle [%s][%s] Async", resourceName, providerName,
                        vehicleId);
        setDefaultUser(CONSUMER);
        endpointUrl = CallsUrl.GET_RESOURCE_VALUE_BY_VEHICLE_ASYNC.builder()
                .withResourceId(resourceName)
                .withProviderName(
                        "hrn:" + NS_Config.REALM.toString() + ":neutral::" + PROVIDER.getUser()
                                .getRealm() + ":" + providerName)
                .withVehicleId(vehicleId)
                .getUrl();
    }

    public InitGetResourceAsyncCall withCampaignId(String campaignId) {
        return withHeader(new Header("CampaignID", campaignId));
    }

    @Override
    public Supplier<Response> defineCall() {
        return () -> RestHelper.post(this, StringUtils.EMPTY);
    }
}
