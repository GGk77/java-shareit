package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> getAllByItem_Owner_IdOrderByStartDesc(Integer userId);

    List<Booking> getAllByBooker_IdOrderByStartDesc(Integer userId);

    Booking getFirstByItem_IdAndEndBeforeOrderByEndDesc(Integer itemId, LocalDateTime end);

    Booking getTopByItem_IdAndStartAfterOrderByStartAsc(Integer itemId, LocalDateTime start);

    List<Booking> getByBooker_IdAndStatus(Integer bookerId, Status status);

    List<Booking> getAllByItem_Owner_IdAndStatus(Integer ownerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :currentTime AND upper(b.status) = UPPER('APPROVED')" +
            "ORDER BY b.start DESC")
    List<Booking> getByBookerIdStatePast(@Param("id") int id, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end >= :currentTime AND :currentTime >= b.start " +
            "ORDER BY b.start DESC")
    List<Booking> getByBookerIdStateCurrent(@Param("userId") int userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :currentTime ORDER BY b.start DESC")
    List<Booking> getFuture(@Param("userId") int userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> getOwnerAll(int ownerId);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE  i.owner.id = :userId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerFuture(@Param("userId") int userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId " +
            "AND b.start <= :currentTime AND b.end >= :currentTime ORDER BY b.start DESC ")
    List<Booking> getOwnerCurrent(@Param("userId") int userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId AND b.end < :currentTime")
    List<Booking> getOwnerPast(@Param("userId") int userId, @Param("currentTime") LocalDateTime currentTime);
}
