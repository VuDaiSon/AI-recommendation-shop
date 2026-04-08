package com.example.recommendershop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "featured_posts")
public class FeaturedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID featuredPostId;
    @Column(name = "url")
    private String url;
    @OneToOne
    @JoinColumn(name = "category_id", unique = true)
    private Category category;
}
