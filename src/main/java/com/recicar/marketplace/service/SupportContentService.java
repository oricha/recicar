package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.BlogPostSummaryDto;
import com.recicar.marketplace.dto.FaqCategoryDto;
import com.recicar.marketplace.dto.FaqEntryDto;
import com.recicar.marketplace.entity.BlogPost;
import com.recicar.marketplace.entity.FaqCategory;
import com.recicar.marketplace.entity.FaqEntry;
import com.recicar.marketplace.repository.BlogPostRepository;
import com.recicar.marketplace.repository.FaqCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SupportContentService {

    private final FaqCategoryRepository faqCategoryRepository;
    private final BlogPostRepository blogPostRepository;

    public SupportContentService(FaqCategoryRepository faqCategoryRepository, BlogPostRepository blogPostRepository) {
        this.faqCategoryRepository = faqCategoryRepository;
        this.blogPostRepository = blogPostRepository;
    }

    public List<FaqCategoryDto> listFaqCategories() {
        return faqCategoryRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    private FaqCategoryDto toCategoryDto(FaqCategory c) {
        List<FaqEntryDto> entries = c.getEntries().stream()
                .map(this::toEntryDto)
                .collect(Collectors.toList());
        return new FaqCategoryDto(c.getSlug(), c.getTitle(), entries);
    }

    private FaqEntryDto toEntryDto(FaqEntry e) {
        return new FaqEntryDto(e.getId(), e.getQuestion(), e.getAnswer());
    }

    public Page<BlogPostSummaryDto> listPublishedBlogSummaries(int page, int size) {
        if (size > 50) {
            size = 50;
        }
        Pageable p = PageRequest.of(Math.max(0, page), Math.max(1, size));
        return blogPostRepository.findByPublishedTrueOrderByPublishedAtDesc(p)
                .map(this::toSummary);
    }

    private BlogPostSummaryDto toSummary(BlogPost post) {
        return new BlogPostSummaryDto(post.getSlug(), post.getTitle(), post.getSummary(), post.getPublishedAt());
    }

    public Optional<BlogPost> findPublishedPostBySlug(String slug) {
        return blogPostRepository.findBySlugAndPublishedTrue(slug);
    }
}
