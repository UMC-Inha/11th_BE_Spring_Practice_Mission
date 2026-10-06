package com.umc.study.controller;

import com.umc.study.service.RentalService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping()
    public String createRental(@RequestBody Map<String, Object> body){
        rentalService.createRental(body);
        return "대여 기록 등록이 완료되었습니다!";
    }
}
