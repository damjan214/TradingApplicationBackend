package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.ReadFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReadFileController {
    private final ReadFileService readFileService;

    @GetMapping("/symbols/get")
    public ResponseEntity<Map<String, String>> getSymbolsAndNames(){
        return ResponseEntity.ok(readFileService.getSymbolsAndNames());
    }
}
