package com.example.food;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WineService {

    @Autowired
    private WineRepository wineRepository;

    public List<Wine> findWinesByPairingFood(String pairingFood) {
        return wineRepository.findByPairingFood(pairingFood);
    }
}
