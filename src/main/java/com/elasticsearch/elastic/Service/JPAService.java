package com.elasticsearch.elastic.Service;

import co.elastic.clients.util.DateTime;
import com.elasticsearch.elastic.Entity.PriceIndex;
import com.elasticsearch.elastic.Repository.PriceIndexRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class JPAService {
    private final PriceIndexRepository priceIndexRepository;

    public JPAService(PriceIndexRepository priceIndexRepository) {
        this.priceIndexRepository = priceIndexRepository;
    }

    public void fetchData() {
        List<PriceIndex> single = priceIndexRepository.getPriceIndexByAdventurePropertyIdAndCountryAndDate(458759,"BD","2025-01-01").toList();
        Optional<DateTime> maxFetchDate = single.stream()
                .map(PriceIndex::getFetchDate) // Extract fetchDate
                .max(Comparator.comparing(DateTime::toInstant));

        maxFetchDate.ifPresent(dateTime ->
                System.out.println("Largest fetchDate: " + dateTime));

        long start = System.currentTimeMillis();
        List<PriceIndex> priceIndexStream = priceIndexRepository.getData().toList();

        HashMap<Integer, PriceIndex> a = new HashMap<>();

        priceIndexStream.forEach(x -> {
            /*System.out.println("=============================");
            System.out.println(x.getAdventurePropertyId()+" "+x.getDate() + " "+x.getFetchDate() );
            System.out.println("=============================");*/
            if (a.containsKey(x.getAdventurePropertyId())) {
                /*System.out.println(x.getAdventurePropertyId()+" time: " + x.getFetchDate());
                System.out.println(x.getAdventurePropertyId()+" hash map time: " + a.get(x.getAdventurePropertyId()).getFetchDate());*/
                if (x.getFetchDate().toEpochMilli() > a.get(x.getAdventurePropertyId()).getFetchDate().toEpochMilli())
                    a.put(x.getAdventurePropertyId(), x);
            } else {
                a.put(x.getAdventurePropertyId(), x);
            }
        });
        long finish = System.currentTimeMillis();
        System.out.println(finish - start);
        System.out.println("Largest fetchDate: "+a.get(458759).getFetchDate());
    }
}
