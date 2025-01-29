package uk.tw.energy.service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import uk.tw.energy.NotFoundException;
import uk.tw.energy.domain.ElectricityReading;

@Service
public class MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    public List<ElectricityReading> getSevenDaysReadings(String smartMeterId) throws NotFoundException {
        var readings = getReadings(smartMeterId);

        if(readings.isPresent()) {
            LocalDateTime currenTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            Instant todayStart = currenTime.toInstant(ZoneOffset.UTC);
            Instant startTime = todayStart.minusMillis(TimeUnit.DAYS.toMillis(0));
            Instant endTime = todayStart.minusMillis(TimeUnit.DAYS.toMillis(7));

            return readings.get().stream().filter(
                    electricityReading -> electricityReading.time().isBefore(startTime) && electricityReading.time().isAfter(endTime))
                    .toList();
        }

        throw new NotFoundException("No meter readings found");
    }

    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!meterAssociatedReadings.containsKey(smartMeterId)) {
            meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
        }
        meterAssociatedReadings.get(smartMeterId).addAll(electricityReadings);
    }
}
