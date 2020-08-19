package com.here.platform.ns.controllers.access;

import com.here.platform.common.config.Conf;
import com.here.platform.ns.dto.DataProvider;
import com.here.platform.ns.dto.ProviderResource;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;


public class VehicleResourceAsyncController extends BaseNeutralServerAccessController<VehicleResourceAsyncController> {

    private final String serviceBasePath = Conf.ns().getNsUrlAccess();

    @Step
    public Response initGetVehicleResouce(DataProvider provider, String vehicleId, ProviderResource resource) {
        return neutralServerAccessClient(serviceBasePath)
                .post("providers/{providerId}/vehicles/{vehicleId}/{resourceId}", provider.generateHrn(), vehicleId, resource.getName());
    }

    @Step
    public Response getVehicleResourceResult(String location) {
        return neutralServerAccessClient(StringUtils.EMPTY)
                .baseUri(location)
                .get();
    }

}
