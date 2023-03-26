package com.danielbenami.cahce.controller;


import com.danielbenami.cahce.model.BondDto;
import com.danielbenami.cahce.service.Map;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
public class CacheController {

    private final Map<String, BondDto> cache;

    public CacheController(Map<String, BondDto> cache) {
        this.cache = cache;
    }

    @GetMapping("/get_bond/{bond_id}")
    public Mono<ResponseEntity<BondDto>> getBond(@PathVariable String bond_id) {
        final BondDto bondDto = cache.get(bond_id);
        if (bondDto == null) return Mono.just(ResponseEntity.status(400).body(null));
        return Mono.just(ResponseEntity.status(200).body(bondDto));
    }


    @PostMapping("/add_bond")
    public void addBond(@Valid @RequestBody BondDto bondDto) {
        cache.put(bondDto.getBondId(), bondDto);
    }


}
