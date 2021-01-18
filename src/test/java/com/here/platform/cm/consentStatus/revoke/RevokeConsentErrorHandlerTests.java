package com.here.platform.cm.consentStatus.revoke;

import static org.assertj.core.api.Assertions.assertThat;

import com.here.platform.cm.consentStatus.BaseConsentStatusTests;
import com.here.platform.cm.controllers.ConsentStatusController.NewConsent;
import com.here.platform.cm.enums.CMErrorResponse;
import com.here.platform.cm.enums.ConsentObject;
import com.here.platform.cm.enums.ConsentRequestContainer;
import com.here.platform.cm.enums.ConsentRequestContainers;
import com.here.platform.cm.enums.MPProviders;
import com.here.platform.cm.steps.api.ConsentRequestSteps;
import com.here.platform.common.ResponseAssertion;
import com.here.platform.common.ResponseExpectMessages.StatusCode;
import com.here.platform.common.annotations.CMFeatures.RevokeConsent;
import com.here.platform.common.annotations.ErrorHandler;
import com.here.platform.common.annotations.Sentry;
import com.here.platform.common.strings.VIN;
import com.here.platform.dataProviders.daimler.DataSubjects;
import com.here.platform.ns.dto.User;
import com.here.platform.ns.dto.Users;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


@RevokeConsent
@DisplayName("Revoke consent")
@Execution(ExecutionMode.SAME_THREAD)
public class RevokeConsentErrorHandlerTests extends BaseConsentStatusTests {

    static Stream<Arguments> consentRequestIdAndVins() {
        return Stream.of(
                Arguments.of("", "", "consentRequestId"),
                Arguments.of("", "test", "consentRequestId")
        );
    }

    @ParameterizedTest(name = "Is not possible to revoke consent requests with crid: {0}, vin: {1}, should cause: {2}")
    @MethodSource("consentRequestIdAndVins")
    @ErrorHandler
    void revokeConsentErrorHandlerTest(String crid, String vin, String cause) {
        MPProviders provider = MPProviders.DAIMLER_REFERENCE;
        DataSubjects dataSubject = DataSubjects.getNextVinLength(provider.getVinLength());
        var privateBearer = dataSubject.getBearerToken();
        var revokedConsentResponse = consentStatusController
                .revokeConsent(
                        NewConsent.builder().consentRequestId(crid).vinHash(new VIN(vin).hashed()).build(),
                        privateBearer
                );

        var actualCause = new ResponseAssertion(revokedConsentResponse)
                .statusCodeIsEqualTo(StatusCode.BAD_REQUEST)
                .expectedErrorResponse(CMErrorResponse.CONSENT_VALIDATION)
                .getCause();
        assertThat(actualCause).isEqualTo(String.format("Consent parameter '%s' must be provided", cause));
    }

    @Test
    @Sentry
    @DisplayName("Verify sentry block revoke ConsentRequest")
    void sentryBlockRevokeConsentRequestTest() {
        MPProviders provider = MPProviders.DAIMLER_REFERENCE;
        User mpConsumer = Users.MP_CONSUMER.getUser();
        ConsentRequestContainer targetContainer = ConsentRequestContainers.generateNew(provider);

        DataSubjects dataSubject = DataSubjects.getNextVinLength(provider.getVinLength());
        String testVin = dataSubject.getVin();

        ConsentObject consentObj = new ConsentObject(mpConsumer, provider, targetContainer);
        var crid = new ConsentRequestSteps(consentObj)
                .onboardAllForConsentRequest()
                .createConsentRequest()
                .addVINsToConsentRequest(testVin)
                .getId();

        final var consentToRevoke = NewConsent.builder()
                .consentRequestId(crid)
                .vinHash(new VIN(testVin).hashed())
                .build();

        consentRequestController.clearBearerToken();
        var revokedConsentResponse = consentStatusController.revokeConsent(consentToRevoke, "");

        new ResponseAssertion(revokedConsentResponse).statusCodeIsEqualTo(StatusCode.BAD_REQUEST);
    }

}
