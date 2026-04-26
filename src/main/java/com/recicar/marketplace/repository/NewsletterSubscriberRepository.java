package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Long> {

    Optional<NewsletterSubscriber> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
