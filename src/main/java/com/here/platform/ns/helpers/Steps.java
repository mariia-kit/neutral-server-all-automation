package com.here.platform.ns.helpers;

import static com.here.platform.ns.dto.Users.PROVIDER;

import com.here.platform.ns.controllers.provider.ContainerController;
import com.here.platform.ns.controllers.provider.ProviderController;
import com.here.platform.ns.controllers.provider.ResourceController;
import com.here.platform.ns.dto.Container;
import com.here.platform.ns.dto.DataProvider;
import com.here.platform.ns.dto.ProviderResource;
import com.here.platform.ns.dto.Providers;
import com.here.platform.ns.restEndPoints.NeutralServerResponseAssertion;
import com.here.platform.ns.restEndPoints.external.AaaCall;
import com.here.platform.ns.restEndPoints.external.MarketplaceManageListingCall;
import com.here.platform.ns.restEndPoints.external.ReferenceProviderCall;
import com.here.platform.ns.restEndPoints.provider.container_info.AddContainerCall;
import com.here.platform.ns.restEndPoints.provider.container_info.DeleteContainerCall;
import com.here.platform.ns.restEndPoints.provider.data_providers.AddDataProviderCall;
import com.here.platform.ns.restEndPoints.provider.data_providers.DeleteDataProviderCall;
import com.here.platform.ns.restEndPoints.provider.resources.AddProviderResourceCall;
import com.here.platform.ns.restEndPoints.provider.resources.DeleteProviderResourceCall;
import com.here.platform.ns.utils.NS_Config;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import javax.mail.Quota;


public class Steps {

    @Step("Create regular Data Provider: {provider.name}")
    public static void createRegularProvider(DataProvider provider) {
        var response = new ProviderController()
                .withToken(PROVIDER)
                .addProvider(provider);
        new NeutralServerResponseAssertion(response)
                .expectedCode(HttpStatus.SC_OK);
        provider.getResources().forEach(res ->
                new ResourceController()
                        .withToken(PROVIDER)
                        .addResource(provider, res)
        );
    }

    @Step("Add resource {res.name} to Data Provider: {provider.name}")
    public static void addResourceToProvider(DataProvider provider, ProviderResource res) {
        var response = new ResourceController()
                .withToken(PROVIDER)
                .addResource(provider, res);
        new NeutralServerResponseAssertion(response)
                .expectedCode(HttpStatus.SC_OK);
    }

    @Step("Remove resources of Data Provider: {provider.name}")
    public static void clearProviderResources(DataProvider provider) {
        provider.getResources().forEach(res -> {
                    var response = new ResourceController()
                            .withToken(PROVIDER)
                            .deleteResource(provider.getName(), res.getName());
                    new NeutralServerResponseAssertion(response)
                            .expectedCode(HttpStatus.SC_NO_CONTENT);
                }
        );
    }

    @Step("Remove regular Data Provider: {provider.name}")
    public static void removeRegularProvider(DataProvider provider) {
        clearProviderResources(provider);
        var response = new ProviderController()
                .withToken(PROVIDER)
                .deleteProvider(provider);
        new NeutralServerResponseAssertion(response)
                .expectedCode(HttpStatus.SC_NO_CONTENT);
    }

    @Step("Create regular Container {container.name} of provider {container.dataProviderName}")
    public static void createRegularContainer(Container container) {
        if (Providers.DAIMLER_REFERENCE.getName().equals(container.getDataProviderName()) ||
                Providers.REFERENCE_PROVIDER.getName().equals(container.getDataProviderName())) {
            ReferenceProviderCall.createContainer(container);
        }
        var response = new ContainerController()
                .withToken(PROVIDER)
                .addContainer(container);
        if ((response.getStatusCode() != HttpStatus.SC_OK) && (response.getStatusCode() != HttpStatus.SC_CONFLICT)) {
            throw new RuntimeException("Error creating container:" + response.getStatusCode());
        }
    }

    @Step("Remove regular Container {container.name} of provider {container.dataProviderName}")
    public static void removeRegularContainer(Container container) {
        var response = new ContainerController()
                .withToken(PROVIDER)
                .deleteContainer(container);
        new NeutralServerResponseAssertion(response)
                .expectedCode(HttpStatus.SC_NO_CONTENT);
    }

    @Step("Create regular Listing for {container.name}")
    public static void createListing(Container container) {
        new MarketplaceManageListingCall()
                .createNewListing(container);
    }

    @Step("Create regular Listing and Subscription for {container.name}")
    public static void createListingAndSubscription(Container container) {
        if (!NS_Config.MARKETPLACE_MOCK.toString().equalsIgnoreCase("true")) {
            String listing = new MarketplaceManageListingCall()
                    .createNewListing(container);
            new MarketplaceManageListingCall()
                    .subscribeListing(listing);
            //new AaaCall().waitForContainerPolicyIntegrationInSentry(container.getDataProviderName(), container.getName());
        } else {
            new AaaCall().createResourcePermission(container);
        }
    }

    @Step("Create regular Listing and Canceled Subscription for {container.name}")
    public static void createListingAndCanceledSubscription(Container container) {
        String listing = new MarketplaceManageListingCall()
                .createNewListing(container);
        String subscription = new MarketplaceManageListingCall()
                .subscribeListing(listing);
        new MarketplaceManageListingCall().beginCancellation(subscription);
    }

    @Step("Create regular Listing and Subscription in progress for {container.name}")
    public static void createListingAndSubscriptionInProgress(Container container) {
        String listing = new MarketplaceManageListingCall()
                .createNewListing(container);
        String subscription = new MarketplaceManageListingCall()
                .subscribeListing(listing);
        new MarketplaceManageListingCall().beginCancellation(subscription);
    }

    @Step("Create and Remove regular Listing and Subscription for {container.name}")
    public static void createListingAndSubscriptionRemoved(Container container) {
        String listing = new MarketplaceManageListingCall()
                .createNewListing(container);
        String subsId = new MarketplaceManageListingCall()
                .subscribeListing(listing);
        new MarketplaceManageListingCall()
                .beginCancellation(subsId);
        new MarketplaceManageListingCall()
                .deleteListing(listing)
                .expectedCode(HttpStatus.SC_NO_CONTENT);
    }

}
