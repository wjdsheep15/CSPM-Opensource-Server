package com.elastic.cspm.data.repository;


import com.elastic.cspm.data.entity.BridgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BridgeEntityRepository extends JpaRepository<BridgeEntity, Long> {
    Optional<List<BridgeEntity>> findAllByMemberEmail(String email);
}
