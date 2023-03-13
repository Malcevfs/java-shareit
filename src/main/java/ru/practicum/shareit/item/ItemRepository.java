package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> getAllByOwnerIdOrderByIdAsc(int ownerId);

    Optional<Item> findItemByIdAndAvailableTrue(int itemId);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailableTrue(String description, Pageable pageable);

    List<Item> findAllByRequestId(int requestId);

    List<Item> findByRequest_IdIn(List<Integer> requestId);

}
