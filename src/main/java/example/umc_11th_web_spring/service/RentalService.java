package example.umc_11th_web_spring.service;

import example.umc_11th_web_spring.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    public void createRental(Map<String, Object> body) {
        // Request Body에서 userId와 bookId 추출 후 Long 변환
        Long userId = Long.valueOf(String.valueOf(body.get("userId")));
        Long bookId = Long.valueOf(String.valueOf(body.get("bookId")));

        rentalRepository.saveRental(userId, bookId);
    }
}