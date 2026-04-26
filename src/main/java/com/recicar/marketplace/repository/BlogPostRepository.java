package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Page<BlogPost> findByPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    Optional<BlogPost> findBySlugAndPublishedTrue(String slug);

    @Query("SELECT p.slug FROM BlogPost p WHERE p.published = true ORDER BY p.publishedAt DESC")
    List<String> findAllPublishedSlugs();
}
