package com.here.platform.e2e;

import static com.codeborne.selenide.Selenide.open;
import static com.here.platform.cm.ui.BaseUITests.getUICmToken;
import static com.here.platform.ns.dto.Users.MP_CONSUMER;
import static com.here.platform.ns.dto.Users.MP_PROVIDER;
import static io.qameta.allure.Allure.step;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.here.platform.cm.enums.MPConsumers;
import com.here.platform.cm.pages.VINEnteringPage;
import com.here.platform.cm.rest.model.ConsentInfo;
import com.here.platform.cm.steps.ui.OfferDetailsPageSteps;
import com.here.platform.cm.steps.ui.SuccessConsentPageSteps;
import com.here.platform.common.VIN;
import com.here.platform.common.VinsToFile;
import com.here.platform.common.config.Conf;
import com.here.platform.common.extensions.ConsentRequestRemoveExtension;
import com.here.platform.common.extensions.UserAccountExtension;
import com.here.platform.dataProviders.daimler.DataSubjects;
import com.here.platform.dataProviders.daimler.steps.DaimlerLoginPage;
import com.here.platform.hereAccount.ui.HereLoginSteps;
import com.here.platform.mp.controllers.ListingsController;
import com.here.platform.mp.models.CreateInvite;
import com.here.platform.mp.pages.ConsumerConsentRequestPage;
import com.here.platform.mp.pages.ConsumerListingPage;
import com.here.platform.mp.pages.ConsumerSubscriptionPage;
import com.here.platform.mp.pages.CreateListingPage;
import com.here.platform.mp.pages.CreateListingPage.ListingType;
import com.here.platform.mp.pages.ListingsListPage;
import com.here.platform.ns.controllers.access.ContainerDataController;
import com.here.platform.ns.dto.Container;
import com.here.platform.ns.dto.Containers;
import com.here.platform.ns.dto.User;
import com.here.platform.ns.restEndPoints.NeutralServerResponseAssertion;
import com.here.platform.ns.restEndPoints.external.MarketplaceManageListingCall;
import com.here.platform.ns.helpers.TokenManager;
import io.qameta.allure.selenide.AllureSelenide;
import io.qameta.allure.selenide.LogType;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
public class E2EUITest extends BaseE2ETest {

    static {
        Configuration.baseUrl = Conf.mp().getMarketplaceUiBaseUrl();
        Configuration.driverManagerEnabled = true;
        Configuration.pollingInterval = 400;
        Configuration.browserSize = "1366x1000";
        SelenideLogger.addListener("AllureListener", new AllureSelenide().enableLogs(LogType.BROWSER, Level.ALL));
    }

    private final ListingsListPage listingsPage = new ListingsListPage();
    private final DataSubjects targetDataSubject = DataSubjects.getNext();
    private final User
            targetDataProvider = MP_PROVIDER.getUser(),
            targetDataConsumer = MP_CONSUMER.getUser();
    private final Container targetContainer = Containers.DAIMLER_EXPERIMENTAL_FUEL.getContainer();
    private final ConsentInfo consentRequest =
            new ConsentInfo()
                    .title(faker.company().buzzword())
                    .purpose(faker.backToTheFuture().quote())
                    .consumerName(MPConsumers.OLP_CONS_1.getConsumerName())
                    .containerName(targetContainer.getName())
                    .containerDescription(targetContainer.getDescription())
                    .resources(List.of(targetContainer.getResourceNames()))
                    .vinLabel(new VIN(targetDataSubject.getVin()).label());
    private final AtomicReference<String> subscriptionId = new AtomicReference<>("");

    @org.testcontainers.junit.jupiter.Container
    public BrowserWebDriverContainer chrome =
            new BrowserWebDriverContainer()
                    .withCapabilities(new ChromeOptions().addArguments("--no-sandbox"))
                    .withRecordingMode(VncRecordingMode.RECORD_FAILING, new File("build/video"));
    @RegisterExtension
    ConsentRequestRemoveExtension consentRequestRemoveExtension = new ConsentRequestRemoveExtension();
    @RegisterExtension
    UserAccountExtension userAccountCleanUpExtension = UserAccountExtension.builder()
            .targetDataSubject(targetDataSubject)
            .build();
    private String listingHrn;

    @BeforeEach
    void setUpBrowserContainer() {
        RemoteWebDriver driver = chrome.getWebDriver();
        WebDriverRunner.setWebDriver(driver);
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(1366, 1000));
    }

    @AfterEach
    void afterEach() {
        if (StringUtils.isBlank(subscriptionId.get())) {
            return;
        }
        TokenManager.resetUserLogins();

        var mpListings = new MarketplaceManageListingCall();
        mpListings.beginCancellation(subscriptionId.get());
        mpListings.deleteListing(listingHrn);
    }

    @Test
    @DisplayName("Simple happy path E2E UI level")
    void simpleHappyPathTest() {
        var listingName = "[E2E test] " + faker.company().buzzword();

        step("Open Marketplace", () -> open("marketplace/provider/listings"));

        step("Login Data Provider", () -> HereLoginSteps.loginMPUser(targetDataProvider));

        step("Create listing for daimler experimental container", () -> {
            listingsPage
                    .isLoaded()
                    .clickCreateListing();
            var createListingPage = new CreateListingPage()
                    .selectListingType(ListingType.NEUTRAL_SERVER)
                    .selectManufacturer(targetContainer.getDataProviderName())
                    .selectDataContainer(targetContainer.getName())
                    .fillListingName(listingName)
                    .fillListingDescription(faker.company().buzzword())
                    .selectAutomotiveTopic()
                    .clickNext();

            step("Select subscription option", () -> {
                createListingPage
                        .addEvaluationSubscription()
                        .describeSubscription(faker.company().catchPhrase())
                        .fillLinkForTermsAndCondiotion(faker.company().url())
                        .clickAddSubscription()
                        .clickNext();
            });

            step("Select publishing options", () -> {
                createListingPage
                        .selectSpecificCustomers()
                        .fillDataConsumerEmail(targetDataConsumer.getEmail())
                        .clickPublish();
            });
        });

        listingHrn = listingsPage.isLoaded()
                .getHrnForListingByName(listingName);

        String providerBearerToken = listingsPage.fetchHereAccessToken();

        var listingController = new ListingsController();
        var createdInvite = listingController
                .withBearerToken(providerBearerToken)
                .inviteConsumerToListing(getInvite(listingHrn, targetDataConsumer));

        step("Logout Data Provider", HereLoginSteps::logout);

        step("Login Data Consumer ", () -> {
            open("marketplace/consumer/listings/");
            HereLoginSteps.loginMPUser(targetDataConsumer);
            listingsPage.isLoaded();
        });

        String consumerBearerToken = listingsPage.fetchHereAccessToken();

        step("Accept invite by data consumer", () -> {
            listingController
                    .withBearerToken(consumerBearerToken)
                    .acceptInvite(createdInvite.getId());
        });

        step("Subscribe to listing Data Consumer", () -> {
            open("marketplace/consumer/listings/");
            listingsPage.isLoaded()
                    .openListingWithName(listingName);

            var consumerListingPage = new ConsumerListingPage().isLoaded();
            consumerListingPage
                    .subscribeToListing()
                    .seeTermsAndConditions()
                    .acceptTermsAndConditions()
                    .startSubscription()
                    .confirmSubscriptionActivation();
        });

        var consentRequestUrl = new AtomicReference<>("");
        step("Create consent request by Data Consumer", () -> {
            var consumerSubscriptionPage = new ConsumerSubscriptionPage().isLoaded();
            subscriptionId.set(getSubscriptionIdFromUrl());

            consumerSubscriptionPage
                    .createConsentRequest()
                    .fillConsentRequestTitle(consentRequest.getTitle())
                    .fillConsentRequestDescription(consentRequest.getPurpose())
                    .attachFileWithVINs(new VinsToFile(targetDataSubject.getVin()).csv())
                    .saveConsentRequest();

            consentRequestUrl.set(new ConsumerConsentRequestPage().isLoaded().copyConsentRequestURLViaClipboard());
        });

        step("Logout Data Consumer", HereLoginSteps::logout);

        step("Open consent request link by Data Subject and login", () -> {
            open(consentRequestUrl.get());
            HereLoginSteps.loginDataSubject(targetDataSubject);
        });

        var crid = getCridFromUrl(consentRequestUrl.get());
        consentRequestRemoveExtension.cridToRemove(crid).vinToRemove(targetDataSubject.getVin());

        step("Fill VIN by Data Subject", () ->
                new VINEnteringPage().isLoaded()
                        .fillVINAndContinue(targetDataSubject.getVin()));

        OfferDetailsPageSteps.verifyConsentDetailsPageAndCountinue(consentRequest);

        this.targetDataSubject.setBearerToken(getUICmToken());

        DaimlerLoginPage.loginDataSubjectOnDaimlerSite(targetDataSubject);
        DaimlerLoginPage.approveDaimlerScopesAndSubmit();

        SuccessConsentPageSteps.verifyFinalPage(consentRequest);

        step("Get vehicle resources by Data Consumer from Data Provider", () -> {
            var response = new ContainerDataController()
                    .withBearerToken(MPConsumers.OLP_CONS_1.generateToken())
                    .withCampaignId(crid)
                    .getContainerForVehicle(
                            targetContainer.getDataProviderByName(),
                            targetDataSubject.getVin(),
                            targetContainer
                    );
            new NeutralServerResponseAssertion(response).expectedCode(200);
        });
    }

    private String getSubscriptionIdFromUrl() {
        var pathSegments = UriComponentsBuilder.fromUriString(WebDriverRunner.url()).build()
                .getPathSegments();

        return pathSegments.get(pathSegments.size() - 2);
    }

    private String getCridFromUrl(String consentRequestUrl) {
        var pathSegments = UriComponentsBuilder.fromUriString(consentRequestUrl).build().getPathSegments();
        return pathSegments.get(pathSegments.size() - 1);
    }

    private CreateInvite getInvite(String listingHrn, User targetUser) {
        return CreateInvite.builder()
                .listingHrn(listingHrn)
                .deliveryMethod("EMAIL")
                .emails(List.of(targetUser.getEmail()))
                .callbackUrl(Conf.mp().getMarketplaceCallbackUrl())
                .build();
    }

}
