package com.example.food;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WineRepository extends JpaRepository<Wine, Integer> {
    List<Wine> findByPairingFood(String pairingFood);
}
