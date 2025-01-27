package jp.fhub.fhub_feeling.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "hospitals")
@Entity
@Getter
@Setter
public class Hospital {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 36, nullable = false)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "postal_code", length = 10, nullable = false)
    private String postalCode;

    @Column(length = 100, nullable = false)
    private String address;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "hospital", fetch = FetchType.LAZY)
    private List<HospitalUser> hospitalUsers;
}
