package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ItemServiceImpl {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    public ItemDto createItem(int userId, Item item) {
        if (item.getAvailable() == null) {
            throw new ItemAviableErrorException("Параметр Available не может быть пустым");
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));


        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
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

    @Transactional
    public ItemBookingDto getItem(int userId, int itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item с id - %x  не найден", itemId)));
        return setComments(setBookings(userId, item), itemId);
    }

    @Transactional
    public Collection<ItemBookingDto> getAllItems(int userId) {
        Collection<ItemBookingDto> userItems = new ArrayList<>();
        for (Item item : itemRepository.getAllByOwnerIdOrderByIdAsc(userId)) {
            userItems.add(setBookings(userId, item));
        }
        return userItems;
    }

    public Collection<ItemDto> searchItem(String text) {
        Collection<ItemDto> items = new ArrayList<>();
        if (text.isEmpty()) {
            return items;
        }
        for (Item item : itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text)) {

            items.add(ItemMapper.toItemDto(item));
        }
        return items;
    }

    private ItemBookingDto setBookings(int userId, Item item) {
        ItemBookingDto itemDtoBooking = ItemMapper.toItemBookingDto(item);
        if (item.getOwner().getId() == userId) {
            itemDtoBooking.setLastBooking(
                    bookingRepository.findAllByItemIdAndEndBeforeOrderByStartDesc(
                            itemDtoBooking.getId(), LocalDateTime.now()
                    ).stream().findFirst().map(BookingMapper::toBookingItemDto).orElse(null));
            itemDtoBooking.setNextBooking(
                    bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(
                            itemDtoBooking.getId(), LocalDateTime.now()
                    ).stream().findFirst().map(BookingMapper::toBookingItemDto).orElse(null));
        }
        return itemDtoBooking;
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

    private ItemBookingDto setComments(ItemBookingDto itemBookingDto, int itemId) {
        List<CommentDto> commentDtos = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemBookingDto.setComments(commentDtos);
        return itemBookingDto;
    }

}


