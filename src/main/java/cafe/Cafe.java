package cafe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pizza.Pizza;

import java.util.List;

@Entity
@Table(name = "cafes")
@NoArgsConstructor
@Getter
@Setter
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String location;

    @Column(length = 30, nullable = false, unique = true)
    private String phone;

    @OneToMany(mappedBy = "cafe", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pizza> pizzas;

}
