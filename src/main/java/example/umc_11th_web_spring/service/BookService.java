package example.umc_11th_web_spring.service;

import example.umc_11th_web_spring.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Map<String, Object>> getAllBooks() {
        return bookRepository.findAll();
    }

    public void createBook(Map<String, Object> body){
        bookRepository.save(body);
    }

    public List<Map<String, Object>> getBooksByCategoryId(Long categoryId){
        return  bookRepository.findAllByCategoryId(categoryId);
    }

    public void createRental(Long userId, Long bookId){
        bookRepository.saveRental(userId, bookId);
    }

    public void updateRental(Long rentalId) {
        bookRepository.updateRental(rentalId);
    }
}