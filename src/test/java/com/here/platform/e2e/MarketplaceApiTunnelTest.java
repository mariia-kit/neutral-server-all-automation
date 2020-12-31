package com.here.platform.e2e;

import static com.here.platform.ns.dto.Users.CONSUMER;
import static com.here.platform.ns.dto.Users.PROVIDER;

import com.here.platform.mp.controllers.MarketplaceTunnelController;
import com.here.platform.ns.controllers.access.ContainerDataController;
import com.here.platform.ns.dto.Container;
import com.here.platform.ns.dto.Containers;
import com.here.platform.ns.dto.DataProvider;
import com.here.platform.ns.dto.Providers;
import com.here.platform.ns.dto.Users;
import com.here.platform.ns.dto.Vehicle;
import com.here.platform.ns.helpers.DefaultResponses;
import com.here.platform.ns.helpers.Steps;
import com.here.platform.ns.instruments.ConsentAfterCleanUp;
import com.here.platform.ns.instruments.MarketAfterCleanUp;
import com.here.platform.ns.restEndPoints.NeutralServerResponseAssertion;
import com.here.platform.ns.restEndPoints.external.ConsentManagementCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceCMAddVinsCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceCMCreateConsentCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceCMGetConsentCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceCMGetConsentStatusCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceManageListingCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceNSGetContainerCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceNSGetContainerInfoCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceNSGetProvidersCall;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@DisplayName("Marketplace integration Tests: 'API Tunnelling'")
@ExtendWith({MarketAfterCleanUp.class, ConsentAfterCleanUp.class})
public class MarketplaceApiTunnelTest extends BaseE2ETest {

    private MarketplaceTunnelController marketplaceTunnelController = new MarketplaceTunnelController();

    @Test
    @Tag("external")
    @Tag("e2e_contract")
    @DisplayName("Verify NS Providers call Successful")
    void verifyNSProvidersCall() {
        DataProvider provider = Providers.generateNew();
        Steps.createRegularProvider(provider);

        var verify = marketplaceTunnelController
                .withConsumerToken()
                .getProvidersList();
        new NeutralServerResponseAssertion(verify)
                .expected(res -> !DefaultResponses.isResponseListEmpty(res),
                        "Expected list should not be empty!")
                .expected(res -> DefaultResponses.isDataProviderPresentInList(provider, res),
                        "No expected container in result!");
    }

    @Test
    @Tag("external")
    @Tag("e2e_contract")
    @DisplayName("Verify NS Container call Successful")
    void verifyNSContainerCall() {
        DataProvider provider = Providers.generateNew();
        Container container = Containers.generateNew(provider)
                .withScope("general:some_scope");

        Steps.createRegularProvider(provider);
        Steps.createRegularContainer(container);

        var verify = marketplaceTunnelController
                .withConsumerToken()
                .getProviderContainers(provider.getName());
        new NeutralServerResponseAssertion(verify)
                .expected(res -> !DefaultResponses.isResponseListEmpty(res),
                        "Expected list should not be empty!")
                .expectedJsonContains("[0].name", container.getName(),
                        "Name of Container not as expected.")
                .expectedJsonContains("[0].dataProviderName", container.getDataProviderName(),
                        "DataProviderName of Container not as expected.")
                .expectedJsonContains("[0].description", container.getDescription(),
                        "Description of Container not as expected.")
                .expectedJsonContains("[0].resourceNames", container.getResourceNames(),
                        "ResourceNames of Container not as expected.")
                .expectedJsonContains("[0].consentRequired",
                        container.getConsentRequired().toString(),
                        "ConsentRequired of Container not as expected.")
                .expectedJsonContains("[0].hrn",
                        "hrn:here-dev:neutral::" + PROVIDER.getUser().getRealm() + ":" + container
                                .getDataProviderName() + "/containers/" + container.getId(),
                        "HRN of Container not as expected.");

    }

    @Test
    @Tag("external")
    @Tag("ignored")
    @DisplayName("Verify NS Container Info call Successful Custom Name")
    void verifyNSContainerInfoCallCustomName() {
        DataProvider provider = Providers.generateNew();
        Container container = Containers.generateNew(provider)
                .withName("Some Custom Name");

        Steps.createRegularProvider(provider);
        Steps.createRegularContainer(container);
        var hrn = marketplaceTunnelController
                .withConsumerToken()
                .getProviderContainers(provider.getName()).getBody().jsonPath().get("hrn").toString()
                .replace("[", "").replace("]", "");

        var verify = marketplaceTunnelController
                .withConsumerToken()
                .getGetContainerInfo(hrn);

        new NeutralServerResponseAssertion(verify)
                .expectedCode(HttpStatus.SC_OK)
                .expectedJsonContains("hrn", container.generateHrn(),
                        "Field HRN not as expected!");
    }

    @Test
    @Tag("external")
    @DisplayName("Verify NS Container call Wrong Token")
    void verifyNSContainerCallWrongToken() {
        DataProvider provider = Providers.generateNew();
        Container container = Containers.generateNew(provider);

        Steps.createRegularProvider(provider);
        Steps.createRegularContainer(container);

        var verify = marketplaceTunnelController
                .withBearerToken(CONSUMER.getToken())
                .getProviderContainers(provider.getName());

        new NeutralServerResponseAssertion(verify)
                .expectedCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @Tag("external")
    @DisplayName("Verify NS Container call No Provider")
    void verifyNSContainerCallNoProvider() {
        DataProvider provider = Providers.generateNew();

        var verify = marketplaceTunnelController
                .withConsumerToken()
                .getProviderContainers(provider.getName());

        new NeutralServerResponseAssertion(verify)
                .expectedCode(HttpStatus.SC_NOT_FOUND)
                .expectedJsonContains("errorCode", "error_external_service",
                        "Field not as expected!")
                .expectedJsonContains("externalServiceError.cause",
                        "Containers for data provider '" + provider.getName() + "' not found",
                        "Field not as expected!")
                .expectedJsonContains("externalServiceError.status", "404",
                        "Field not as expected!");
    }

    @Test
    @Tag("external")
    @DisplayName("Verify NS Container call Empty Provider")
    void verifyNSContainerCallEmptyProvider() {
        DataProvider provider = Providers.generateNew();
        Steps.createRegularProvider(provider);

        var verify = marketplaceTunnelController
                .withConsumerToken()
                .getProviderContainers(provider.getName());

        new NeutralServerResponseAssertion(verify)
                .expectedCode(HttpStatus.SC_NOT_FOUND)
                .expectedJsonContains("errorCode", "error_external_service",
                        "Field not as expected!")
                .expectedJsonContains("externalServiceError.cause",
                        "Containers for data provider '" + provider.getName() + "' not found",
                        "Field not as expected!")
                .expectedJsonContains("externalServiceError.status", "404",
                        "Field not as expected!");
    }


    @Test
    @Tag("external")
    @Tag("e2e_contract")
    @DisplayName("Verify NS Get Container info call Successful")
    void verifyNSGetContainerInfoCall() {
        DataProvider provider = Providers.generateNew();
        Container container = Containers.generateNew(provider)
                .withScope("general:some_scope");

        Steps.createRegularProvider(provider);
        Steps.createRegularContainer(container);

        var verify = marketplaceTunnelController
                .withConsumerToken()
                .getGetContainerInfo(container.generateHrn());

        new NeutralServerResponseAssertion(verify)
                .expectedCode(HttpStatus.SC_OK)
                .expectedJsonContains("hrn", container.generateHrn(),
                        "Field HRN not as expected!")
                .expectedJsonContains("name", container.getName(),
                        "Field Name not as expected!");
    }

    @Test
    @Tag("external")
    @Tag("ignored-dev")
    @DisplayName("Verify CM create consent call Successful")
    void verifyCMCreateConsentCall() {
        DataProvider provider = Providers.REFERENCE_PROVIDER.getProvider();
        Container container = Containers.generateNew(provider);

        Steps.createRegularProvider(provider);
        Steps.createRegularContainer(container);
        String listing = new MarketplaceManageListingCall()
                .createNewListing(container);
        String subs = new MarketplaceManageListingCall()
                .subscribeListing(listing);
        new ConsentManagementCall().addCMApplication(container, provider.getName());

        String consentRequestId = new MarketplaceCMCreateConsentCall(subs, container)
                .call()
                .expectedCode(HttpStatus.SC_CREATED)
                .getResponse().jsonPath().get("consentRequestId");

        new MarketplaceCMGetConsentCall(subs)
                .call()
                .expectedCode(HttpStatus.SC_OK)
                .expectedJsonContains("consentRequestId", consentRequestId, "Consent id in response not as expected");

        new MarketplaceCMGetConsentStatusCall(subs)
                .call().expectedCode(HttpStatus.SC_OK)
                .expectedJsonContains("pending", "0", "Consent status value not as expected");

    }

    @Test
    @Tag("external")
    @Tag("ignored-dev")
    @DisplayName("Verify CM Create Consent Call")
    void verifyCMCreateConsentFullCall() {
        DataProvider provider = Providers.REFERENCE_PROVIDER.getProvider();
        Container container = Containers.generateNew(provider);

        Steps.createRegularProvider(provider);
        Steps.createRegularContainer(container);


        String listing = new MarketplaceManageListingCall()
                .createNewListing(container);
        String subs = new MarketplaceManageListingCall()
                .subscribeListing(listing);
        new ConsentManagementCall().addCMApplication(container, provider.getName());


        String consentRequestId = new MarketplaceCMCreateConsentCall(subs, container)
                .call()
                .expectedCode(HttpStatus.SC_CREATED)
                .getResponse().jsonPath().get("consentRequestId");

        new ConsentManagementCall().addVinNumbers(consentRequestId, Vehicle.validVehicleId);

        //todo refactor authorisation to reuse HereUserManagerController
        Response res = new ConsentManagementCall()
                .approveConsentRequestNew(consentRequestId, Vehicle.validVehicleId, Users.HERE_USER.getToken(),
                        container);

        Assertions.assertEquals(HttpStatus.SC_OK, res.getStatusCode(),
                "Error during approve of consent " + consentRequestId + " for vin " + Vehicle.validVehicleId);

        new MarketplaceCMGetConsentStatusCall(subs)
                .call().expectedCode(HttpStatus.SC_OK)
                .expectedJsonContains("approved", "1", "Consent status value not as expected");

        var response = new ContainerDataController()
                .withToken(CONSUMER)
                .withConsentId(consentRequestId)
                .getContainerForVehicle(provider, Vehicle.validVehicleId, container);
        new NeutralServerResponseAssertion(response)
                .expectedCode(HttpStatus.SC_OK);

    }

    @Test
    @Tag("external")
    @Tag("ignored-dev")
    @DisplayName("Verify CM Create Consent Call with TOU")
    void verifyCMCreateConsentFullCallTOU() {
        DataProvider provider = Providers.REFERENCE_PROVIDER.getProvider();
        Container container = Containers.generateNew(provider);

        Steps.createRegularProvider(provider);
        Steps.createRegularContainer(container);
        String listing = new MarketplaceManageListingCall()
                .createNewListing(container);
        String subs = new MarketplaceManageListingCall()
                .subscribeListing(listing);
        new ConsentManagementCall().addCMApplication(container, provider.getName());

        String consentRequestId = new MarketplaceCMCreateConsentCall(subs, container)
                .call()
                .expectedCode(HttpStatus.SC_CREATED)
                .getResponse().jsonPath().get("consentRequestId");

        new ConsentManagementCall().addVinNumbers(consentRequestId, Vehicle.validVehicleId);

        new ConsentManagementCall().getConsentInfo(Vehicle.validVehicleId);
    }

    @Test
    @Tag("external")
    @Tag("ignored-dev")
    @DisplayName("Verify CM add vin numbers file call Successful")
    void verifyCMAddVinFileCall() {
        DataProvider provider = Providers.REFERENCE_PROVIDER.getProvider();
        Container container = Containers.generateNew(provider);

        Steps.createRegularProvider(provider);
        Steps.createRegularContainer(container);
        String listing = new MarketplaceManageListingCall()
                .createNewListing(container);
        String subs = new MarketplaceManageListingCall()
                .subscribeListing(listing);
        new ConsentManagementCall().addCMApplication(container, provider.getName());

        new MarketplaceCMCreateConsentCall(subs, container)
                .call()
                .expectedCode(HttpStatus.SC_CREATED)
                .getResponse().jsonPath().get("consentRequestId");

        new MarketplaceCMAddVinsCall(subs, Vehicle.validVehicleId)
                .call()
                .expectedCode(HttpStatus.SC_OK);

        new MarketplaceCMGetConsentStatusCall(subs)
                .call()
                .expectedCode(HttpStatus.SC_OK)
                .expectedJsonContains("pending", "1", "Consent status value not as expected");

    }

}
