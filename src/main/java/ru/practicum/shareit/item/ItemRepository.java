package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository <Item, Integer> {
    @Transactional
    List<Item> findItemsByOwner (int ownerId);
    @Transactional
    Optional<Item> findItemByIdAndAvailableTrue(int itemId);
    @Transactional
    Optional<Item> findItemByOwnerAndId (int ownerId, int itemId);
    @Transactional
    List<Item> findByDescriptionContainingIgnoreCaseAndAvailableTrue(String description);

}
