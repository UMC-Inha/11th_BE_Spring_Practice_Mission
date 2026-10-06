package example.umc_11th_web_spring.service;

import example.umc_11th_web_spring.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    public void createRental(Long userId, Long bookId) {
        rentalRepository.save(userId, bookId);
    }

    public void returnRental(Long rentalId) {
        int updatedCount = rentalRepository.updateReturnedAt(rentalId);

        if (updatedCount == 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "존재하지 않거나 이미 반납된 대여 기록입니다."
            );
        }
    }
}