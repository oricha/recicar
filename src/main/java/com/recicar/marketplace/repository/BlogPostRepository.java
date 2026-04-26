package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Page<BlogPost> findByPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    Optional<BlogPost> findBySlugAndPublishedTrue(String slug);
}
