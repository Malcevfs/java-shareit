package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public ItemDto createItem(int userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ItemAviableErrorException("Параметр Available не может быть пустым");
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));
        ItemRequest itemRequest = null;

        if (itemDto.getRequestId() != 0) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new UserNotFoundErrorException(String.format("ItemRequest с id - %x не найден", itemDto.getRequestId())));
        }
        Item item = ItemMapper.toItem(itemDto, user, itemRequest);
        item.setOwner(user);
        itemRepository.save(item);
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Transactional
    public ItemDto update(int userId, int itemId, Item item) {
        Item newItem = itemRepository.findById(itemId).orElseThrow(() ->
                new OwnerErrorException(String.format("Item с id - %x  не найден", itemId)));

        if (newItem.getOwner().getId() == userId) {
            if (item.getDescription() != null) {
                newItem.setDescription(item.getDescription());
            }
            if (item.getName() != null) {
                newItem.setName(item.getName());
            }
            if (item.getAvailable() != null) {
                newItem.setAvailable(item.getAvailable());
            }
        } else {
            throw new ItemNotFoundException("Вещь для обновления не найдена");
        }
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    public ItemBookingDto getItem(int userId, int itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item с id - %x  не найден", itemId)));
        Map<Integer, List<Booking>> lastBookings = getAllLastBookingsByItemId(Set.of(itemId), userId);
        Map<Integer, List<Booking>> nextBookings = getAllNextBookingsByItemId(Set.of(itemId), userId);

        ItemBookingDto itemResponseDto = ItemMapper.toItemBookingDto(item);

        setBookingsToDTO(
                Optional.ofNullable(lastBookings.get(item.getId())),
                Optional.ofNullable(nextBookings.get(item.getId())),
                itemResponseDto
        );
        setCommentsToDTO(itemResponseDto);

        return itemResponseDto;
    }

    public Collection<ItemBookingDto> getAllItems(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));
        List<Item> items = itemRepository.getAllByOwnerIdOrderByIdAsc(userId);

        Set<Integer> ids = items.stream().map(Item::getId).collect(Collectors.toSet());
        Map<Integer, List<Booking>> lastBookings = getAllLastBookingsByItemId(ids, user.getId());
        Map<Integer, List<Booking>> nextBookings = getAllNextBookingsByItemId(ids, user.getId());

        return items.stream().map(item -> {
            ItemBookingDto itemResponseDto = ItemMapper.toItemBookingDto(item);
            setBookingsToDTO(
                    Optional.ofNullable(lastBookings.get(item.getId())),
                    Optional.ofNullable(nextBookings.get(item.getId())),
                    itemResponseDto
            );
            setCommentsToDTO(itemResponseDto);

            return itemResponseDto;
        }).collect(Collectors.toList());
    }

    public Collection<ItemDto> searchItem(String text, int from, int size) {
        Collection<ItemDto> items = new ArrayList<>();
        if (text.isEmpty()) {
            return items;
        }
        for (Item item : itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text, PageRequest.of(from, size))) {

            items.add(ItemMapper.toItemDto(item));
        }
        return items;
    }

    private void setBookingsToDTO(
            Optional<List<Booking>> lastBookings, Optional<List<Booking>> nextBookings, ItemBookingDto itemResponseDto
    ) {
        itemResponseDto
                .setLastBooking(lastBookings.flatMap(bookings -> Optional.ofNullable(bookings.get(0))
                                .map(BookingMapper::toBookingItemDto))
                        .orElse(null));

        itemResponseDto
                .setNextBooking(nextBookings.flatMap(bookings -> Optional.ofNullable(bookings.get(0))
                                .map(BookingMapper::toBookingItemDto))
                        .orElse(null));
    }

    private Map<Integer, List<Booking>> getAllLastBookingsByItemId(Set<Integer> itemIds, Integer userId) {
        return bookingRepository.findByItemIdAndOwnerIdAndStartDateLessThenNowInOrderByIdDesc(
                        itemIds, userId, LocalDateTime.now()).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private Map<Integer, List<Booking>> getAllNextBookingsByItemId(Set<Integer> itemIds, Integer userId) {
        return bookingRepository.findByItemIdAndOwnerIdAndStartDateIsMoreThenNowInOrderByIdAsc(
                        itemIds, userId, LocalDateTime.now())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private void setCommentsToDTO(ItemBookingDto itemResponseDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemResponseDto.getId());

        itemResponseDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    @Transactional
    public CommentDto addComment(int userId, int itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

        Item item = itemRepository.findItemByIdAndAvailableTrue(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item с id - %x  не найден", itemId)));

        bookingRepository.findFirstByBookerAndItemIdAndEndBefore(author, itemId, LocalDateTime.now()).orElseThrow(() ->
                new BadRequestException("Предмет не был забронирован"));

        Comment comment = CommentMapper.toComment(commentDto, author, item);
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

}


