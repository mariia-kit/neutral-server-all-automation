package com.here.platform.cm.enums;

import static com.here.platform.cm.enums.ConsentRequestContainers.RealDaimlerApplication.CLIENT_ID;
import static com.here.platform.cm.enums.ConsentRequestContainers.RealDaimlerApplication.CLIENT_SECRET;
import static com.here.platform.common.strings.SBB.sbb;

import com.github.javafaker.Faker;
import com.here.platform.common.config.Conf;
import com.here.platform.ns.dto.Container;
import com.here.platform.ns.dto.Containers;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;


/**
 * Possible to use as a container name for provider's applications
 */
@ToString
@Getter
public enum ConsentRequestContainers {

    //todo: refactor to reuse all data

    PAY_AS_YOU_DRIVE(
            "payasyoudrive", "Pay as you drive insurance", "Pay as you drive insurance",
            "mb:vehicle:mbdata:payasyoudrive offline_access",
            CLIENT_ID, CLIENT_SECRET,
            List.of("odometer"),
            MPProviders.DAIMLER
    ),
    VEHICLE_STATUS(
            "vehiclestatus", "Vehicle status", "Vehicle status",
            "mb:vehicle:mbdata:vehiclestatus offline_access",
            CLIENT_ID, CLIENT_SECRET,
            List.of("odometer"),
            MPProviders.DAIMLER
    ),
    ELECTRIC_VEHICLE_STATUS(
            "electricvehicle", "Electric vehicle status", "Electric vehicle status",
            "mb:vehicle:mbdata:evstatus offline_access",
            CLIENT_ID, CLIENT_SECRET,
            List.of("odometer"),
            MPProviders.DAIMLER
    ),
    FUEL_STATUS(
            "fuelstatus", "Fuel status", "Fuel status",
            "mb:vehicle:mbdata:fuelstatus offline_access",
            CLIENT_ID, CLIENT_SECRET,
            List.of("odometer"),
            MPProviders.DAIMLER
    ),
    VEHICLE_LOCK_STATUS(
            "vehiclelockstatus", "Vehicle lock status", "Vehicle lock status",
            "mb:vehicle:mbdata:vehiclelock offline_access",
            CLIENT_ID, CLIENT_SECRET,
            List.of("odometer"),
            MPProviders.DAIMLER
    ),
    DAIMLER_EXPERIMENTAL_ODOMETER(
            "odometer", "odometer",
            "Provides odometer specific information.",
            "mb:user:pool:reader mb:vehicle:status:general offline_access",
            Conf.cmUsers().getDaimlerApp().getClientId(),
            Conf.cmUsers().getDaimlerApp().getClientSecret(),
            List.of("odometer"),
            MPProviders.DAIMLER_EXPERIMENTAL
    ),
    DAIMLER_EXPERIMENTAL_FUEL(
            "fuel", "fuel",
            "Provides fuel specific information.",
            "mb:user:pool:reader mb:vehicle:status:general offline_access",
            Conf.cmUsers().getDaimlerApp().getClientId(),
            Conf.cmUsers().getDaimlerApp().getClientSecret(),
            List.of("fuel"),
            MPProviders.DAIMLER_EXPERIMENTAL
    ),
    DAIMLER_EXPERIMENTAL_TIRES(
            "tires", "tires",
            "Provides information about the tire pressure.",
            "mb:user:pool:reader mb:vehicle:status:general offline_access",
            DAIMLER_EXPERIMENTAL_ODOMETER.clientId,
            DAIMLER_EXPERIMENTAL_ODOMETER.clientSecret,
            List.of("tires"),
            MPProviders.DAIMLER_EXPERIMENTAL
    ),
    DAIMLER_EXPERIMENTAL_DOORS(
            "doors", "doors",
            "Provides information about the doors status.",
            "mb:user:pool:reader mb:vehicle:status:general offline_access",
            DAIMLER_EXPERIMENTAL_ODOMETER.clientId,
            DAIMLER_EXPERIMENTAL_ODOMETER.clientSecret,
            List.of("doors"),
            MPProviders.DAIMLER_EXPERIMENTAL
    ),
    DAIMLER_EXPERIMENTAL_LOCATION(
            "location", "location",
            "Provides location information about vehicle.",
            "mb:user:pool:reader mb:vehicle:status:general offline_access",
            DAIMLER_EXPERIMENTAL_ODOMETER.clientId,
            DAIMLER_EXPERIMENTAL_ODOMETER.clientSecret,
            List.of("location"),
            MPProviders.DAIMLER_EXPERIMENTAL
    ),
    DAIMLER_EXPERIMENTAL_CHARGE(
            "stateofcharge", "stateofcharge",
            "Provides charge status of the battery pack.",
            "mb:user:pool:reader mb:vehicle:status:general offline_access",
            DAIMLER_EXPERIMENTAL_ODOMETER.clientId,
            DAIMLER_EXPERIMENTAL_ODOMETER.clientSecret,
            List.of("stateofcharge"),
            MPProviders.DAIMLER_EXPERIMENTAL
    ),
    BMW_MILEAGE(
            "payasyoudrive", "bmwcardata_mileage", "Vehicle mileage",
            "",
            "S00I000M001OK", "no secret",
            List.of("mileage"),
            MPProviders.BMW_TEST
    ),
    BMW_DOORS_STATUS(
            "doorsstatus", "bmwcardata_doors", "Status of doors",
            "",
            "Z00G010J992HM", StringUtils.EMPTY,
            List.of("doorsstatus"),
            MPProviders.BMW
    ),
    DAIMLER_REFERENCE(
            "odometer", "odometer", "Automated Test Container",
            "mb:vehicle:mbdata:payasyoudrive offline_access",
            Conf.ns().getReferenceApp().getClientId(),
            Conf.ns().getReferenceApp().getClientSecret(),
            List.of("odometer"),
            MPProviders.DAIMLER_REFERENCE
    ),
    DAIMLER_REFERENCE_PROD(
            "odometer", "odometer", "Automated Test Container",
            "mb:vehicle:mbdata:payasyoudrive offline_access",
            Conf.ns().getReferenceApp().getClientId(),
            Conf.ns().getReferenceApp().getClientSecret(),
            List.of("fuel"),
            MPProviders.REFERENCE_PROD
    ),
    REFERENCE_NEW(
            "odometer", "odometer", "Automated Test Container",
            "mb:vehicle:mbdata:payasyoudrive offline_access",
            Conf.ns().getReferenceApp().getClientId(),
            Conf.ns().getReferenceApp().getClientSecret(),
            List.of("odometer"),
            MPProviders.DAIMLER_REFERENCE
    );

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    public String id, name, containerDescription, scopeValue, clientId, clientSecret;
    //TODO implement reusing of provider from container for consent request creation and onboarding
    public List<String> resources;
    public MPProviders provider;

    ConsentRequestContainers(
            String id, String name, String containerDescription, String scopeValue,
            String clientId, String clientSecret, List<String> resources, MPProviders provider
    ) {
        this.id = id;
        this.name = name;
        this.containerDescription = containerDescription;
        this.scopeValue = scopeValue;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.resources = resources;
        this.provider = provider;
    }

    public static ConsentRequestContainers generateRandomContainer() {
        var faker = new Faker();

        var daimlerExperimental = List.of(
                DAIMLER_EXPERIMENTAL_ODOMETER, DAIMLER_EXPERIMENTAL_TIRES, DAIMLER_EXPERIMENTAL_LOCATION,
                DAIMLER_EXPERIMENTAL_FUEL, DAIMLER_EXPERIMENTAL_DOORS, DAIMLER_EXPERIMENTAL_CHARGE
        );

        var newRandomContainer = daimlerExperimental.get((int) (Math.random() * daimlerExperimental.size() - 1));
        newRandomContainer.id = faker.crypto().sha256();
        newRandomContainer.name = newRandomContainer.id;
        return newRandomContainer;
    }

    public static ConsentRequestContainers getNextDaimlerExperimental() {
        var daimlerExperimentalContainers = List.of(
                DAIMLER_EXPERIMENTAL_ODOMETER, DAIMLER_EXPERIMENTAL_TIRES, DAIMLER_EXPERIMENTAL_LOCATION,
                DAIMLER_EXPERIMENTAL_FUEL, DAIMLER_EXPERIMENTAL_DOORS, DAIMLER_EXPERIMENTAL_CHARGE
        );
        if (atomicInteger.getAcquire() > daimlerExperimentalContainers.size() - 1) {
            atomicInteger.set(0);
        }
        return daimlerExperimentalContainers.get(atomicInteger.getAndIncrement());
    }

    public static ConsentRequestContainers getById(String containerId) {
        var consentRequestContainers = Arrays.stream(values())
                .filter(containers -> containers.id.equals(containerId)).findFirst();

        var mockContainer = REFERENCE_NEW;
        mockContainer.id = containerId;
        mockContainer.name = containerId;
        mockContainer.containerDescription = Conf.cm().getQaTestDataMarker() + containerId;

        return consentRequestContainers.orElse(mockContainer);
    }

    public static ConsentRequestContainer generateNew(MPProviders targetProvider) {
        Container container = Containers.generateNew(targetProvider.getName())
                .withDescription(sbb(Conf.cm().getQaTestDataMarker()).append("cm_automated_container").bld());
        return ConsentRequestContainer.builder()
                .id(container.getId())
                .name(container.getName())
                .scopeValue(container.getScope())
                .resources(List.of(container.getResourceNames()))
                .containerDescription(container.getDescription())
                .provider(targetProvider)
                .clientId(Conf.ns().getReferenceApp().getClientId())
                .clientSecret(Conf.ns().getReferenceApp().getClientSecret())
                .build();
    }

    public ConsentRequestContainer getConsentContainer() {
        return ConsentRequestContainer.builder()
                .id(this.getId())
                .name(this.getName())
                .scopeValue(this.getScopeValue())
                .containerDescription(this.getContainerDescription())
                .resources(this.getResources())
                .provider(this.getProvider())
                .clientId(this.getClientId())
                .clientSecret(this.getClientSecret())
                .build();
    }

    @AllArgsConstructor
    public enum ConnectedVehicleCredentials {
        LOCAL("7ad8cbff-d257-4182-b41f-2a4afd013e47", "9d9b8e0a-04b3-4a78-aa8d-8143ccd0e6f3"),
        DEV("7ad8cbff-d257-4182-b41f-2a4afd013e47", "9d9b8e0a-04b3-4a78-aa8d-8143ccd0e6f3"),
        SIT("837df42f-500f-4b3d-be3d-c57e11b61f45", "240c3498-d266-4b30-a837-7d7220987cef"),
        PROD("93dc654d-eabe-4437-a3ae-fdb0ce32b58b", "3b789e8a-e472-4cd5-875a-f4c773f4bc4d");
        String clientId, clientSecret;
    }

    public interface RealDaimlerApplication {

        String CLIENT_ID = "15998dfd-a3e2-4dae-94bb-745bdf3351af";
        String CLIENT_SECRET = "08c7e4c3-0fed-485f-b634-6d95093ec689";

    }

}
