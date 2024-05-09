package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.repository.StockRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
}
