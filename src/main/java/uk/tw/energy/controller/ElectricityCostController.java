package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/electricity-cost")
public class ElectricityCostController {

    private final MeterReadingService meterReadingService;
    private final PricePlanService pricePlanService;
    private final AccountService accountService;
    private final List<PricePlan> pricePlans;

    public ElectricityCostController(MeterReadingService meterReadingService, PricePlanService pricePlanService, AccountService accountService, List<PricePlan> pricePlans) {
        this.meterReadingService = meterReadingService;
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
        this.pricePlans = pricePlans;
    }

    @GetMapping("/last-week/{smartMeterId}")
    public ResponseEntity<BigDecimal> readReadings(@PathVariable String smartMeterId) {
        List<ElectricityReading> sevenDaysReadings = null;
        try {
            sevenDaysReadings = meterReadingService.getSevenDaysReadings(smartMeterId);
            var pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
            var pricePlan = pricePlans.stream().filter(p -> p.getPlanName().equals(pricePlanId)).findFirst();

            if (pricePlan.isPresent()) {
                return ResponseEntity.ok(pricePlanService.calculateCost(sevenDaysReadings, pricePlan.get()));
            }

            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
