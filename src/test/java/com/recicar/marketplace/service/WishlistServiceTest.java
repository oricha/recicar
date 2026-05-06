package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.WishlistItem;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.WishlistItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    void addItem_returnsExistingCount_whenAlreadyPresent() {
        when(productRepository.existsById(5L)).thenReturn(true);
        when(wishlistItemRepository.findByUser_IdAndProduct_Id(1L, 5L)).thenReturn(Optional.of(new WishlistItem()));
        when(wishlistItemRepository.countByUser_Id(1L)).thenReturn(3);

        int c = wishlistService.addItem(1L, 5L);

        assertThat(c).isEqualTo(3);
        verify(wishlistItemRepository, never()).save(any(WishlistItem.class));
    }

    @Test
    void addItem_saves_whenNew() {
        User user = new User();
        user.setId(2L);
        Product product = new Product();
        product.setId(8L);

        when(productRepository.existsById(8L)).thenReturn(true);
        when(wishlistItemRepository.findByUser_IdAndProduct_Id(2L, 8L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(productRepository.getReferenceById(8L)).thenReturn(product);
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(wishlistItemRepository.countByUser_Id(2L)).thenReturn(1);

        int c = wishlistService.addItem(2L, 8L);

        assertThat(c).isEqualTo(1);
        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    void removeItem_returnsCount_afterDelete() {
        when(wishlistItemRepository.countByUser_Id(1L)).thenReturn(0);

        int c = wishlistService.removeItem(1L, 9L);

        assertThat(c).isZero();
        verify(wishlistItemRepository).deleteByUser_IdAndProduct_Id(1L, 9L);
    }

    @Test
    void mergeSessionWishlistIntoDatabase_noOpWhenSessionNull() {
        wishlistService.mergeSessionWishlistIntoDatabase(null, 1L);

        verifyNoInteractions(wishlistItemRepository);
    }

    @Test
    void mergeSessionWishlistIntoDatabase_skipsDuplicateProductRows() {
        jakarta.servlet.http.HttpSession session = mock(jakarta.servlet.http.HttpSession.class);
        User user = new User();
        user.setId(11L);

        Set<Long> sessionIds = new LinkedHashSet<>();
        sessionIds.add(100L);

        when(session.getAttribute(WishlistService.SESSION_WISHLIST_KEY)).thenReturn(sessionIds);
        when(productRepository.existsById(100L)).thenReturn(true);
        when(wishlistItemRepository.findByUser_IdAndProduct_Id(11L, 100L)).thenReturn(Optional.of(new WishlistItem()));
        when(wishlistItemRepository.findProductIdsByUserId(11L)).thenReturn(Collections.emptyList());

        wishlistService.mergeSessionWishlistIntoDatabase(session, 11L);

        verify(wishlistItemRepository, never()).save(any(WishlistItem.class));
        verify(session).setAttribute(eq(WishlistService.SESSION_WISHLIST_KEY), any());
    }
}
