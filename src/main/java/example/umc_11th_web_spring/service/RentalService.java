package example.umc_11th_web_spring.service;

import example.umc_11th_web_spring.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    public void createRental(Long userId, Long bookId) {
        rentalRepository.save(userId, bookId);
    }

    public void returnRental(Long rentalId) {
        rentalRepository.updateReturnedAt(rentalId);
    }
}