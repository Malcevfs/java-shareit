package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    public List<ItemRequest> findAllByRequesterIdIsNotOrderByCreatedDesc(int userId, Pageable pageable);
    public List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(int userId);


}
