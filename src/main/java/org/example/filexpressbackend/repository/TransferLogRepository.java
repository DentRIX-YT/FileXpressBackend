package org.example.filexpressbackend.repository;

import org.example.filexpressbackend.entity.TransferLog;
import org.example.filexpressbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferLogRepository extends JpaRepository<TransferLog, Long> {

    List<TransferLog> findBySender(User sender);

    List<TransferLog> findByReceiver(User receiver);

    List<TransferLog> findBySenderOrReceiver(User sender, User receiver);
}
