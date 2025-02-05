package uk.tw.energy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.generator.ElectricityReadingsGenerator;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class ElectricityCostControllerTest {
    private MeterReadingService meterReadingService;
    private PricePlanService pricePlanService;
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        meterReadingService = new MeterReadingService(generateMeterReadingService());
        pricePlanService = new PricePlanService(generatePricePlanServiceService(), meterReadingService);
        accountService = new AccountService(generateSmartMeterToPricePlanAccounts());
    }

    @Test
    public void shouldReturnNotFoundWhenSendingRandomMeterId() {
        ElectricityCostController controller = new ElectricityCostController(meterReadingService, pricePlanService, accountService, generatePricePlanServiceService());
        var response = controller.readReadings("");

        System.out.println(response);
    }

    private Map<String, List<ElectricityReading>> generateMeterReadingService() {
        ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        Map<String, List<ElectricityReading>> readings = new HashMap<>();
        readings.put("MOCK_METER_ID", electricityReadingsGenerator.generate(200));

        return readings;
    }

    private List<PricePlan> generatePricePlanServiceService() {
        final List<PricePlan> pricePlans = new ArrayList<>();
        pricePlans.add(new PricePlan("MOCK_METER_ID", "Dr Evil's Dark Energy", BigDecimal.TEN, emptyList()));
        return pricePlans;
    }

    private Map<String, String> generateSmartMeterToPricePlanAccounts() {
        final Map<String, String> smartMeterToPricePlanAccounts = new HashMap<>();
        smartMeterToPricePlanAccounts.put("MOCK_METER_ID", "MOST_EVIL_PRICE_PLAN_ID");
        return smartMeterToPricePlanAccounts;
    }
}
