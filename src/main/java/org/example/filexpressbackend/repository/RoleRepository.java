package org.example.filexpressbackend.repository;


import org.example.filexpressbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :id")
    List<Role> findRolesByUserId(Long id);


    Role findByRoleName(String role);

}
