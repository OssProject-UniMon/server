package dongguk.capstone.backend.scholarship.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "scholarship")
public class Scholarship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scholarship_id")
    private Long scholarshipId;

    @Column(length = 300)
    private String name;

    @Lob // LONGTEXT는 @Lob 애노테이션을 사용하여 매핑한다.
    @Column(columnDefinition = "LONGTEXT") // LONGTEXT를 위해서는 columnDefinition도 있어야 한다.
    private String amount;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String target;

    @Column(length = 200)
    private String due;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String url;
}
