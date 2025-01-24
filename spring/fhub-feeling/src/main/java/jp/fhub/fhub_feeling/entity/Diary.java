package jp.fhub.fhub_feeling.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "diaries")
@Entity
@Getter
@Setter
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 36, nullable = false)
    private UUID id;

    @Column(name = "mood_score", nullable = false)
    private Integer moodScore;

    @Column(name = "good_contents", length = 255, nullable = false)
    private String goodContents;

    @Column(length = 255, nullable = false)
    private String contents;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
