package cafe;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CafeService {

    @Autowired
    private CafeRepository cafeRepository;

    public List<Cafe> getAllCafes(){
        return cafeRepository.findAll();
    }

    public Cafe getCafeById(Long id){
        return cafeRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Cafe with ID " + id + " not found"));
    }

    public Cafe createCafe(Cafe cafe){
        validateCafe(cafe);
        return cafeRepository.save(cafe);
    }

    public Cafe updateCafe(Long id, Cafe updatedCafe){
        Cafe existingCafe = cafeRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Cafe with ID " + id + " not found"));
        existingCafe.setName(updatedCafe.getName());
        existingCafe.setLocation(updatedCafe.getLocation());
        existingCafe.setPhone(updatedCafe.getPhone());
        return cafeRepository.save(existingCafe);
    }

    public void deleteCafe(Long id){
        if (!cafeRepository.existsById(id))
            throw new EntityNotFoundException("Cafe with ID " + id + " not exists");
        cafeRepository.deleteById(id);
    }

    private void validateCafe(Cafe cafe){
        if (cafe == null)
            throw new IllegalArgumentException("Cafe cannot be null");
    }
}
