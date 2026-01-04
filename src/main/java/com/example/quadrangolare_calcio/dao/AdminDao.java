package com.example.quadrangolare_calcio.dao;

import com.example.quadrangolare_calcio.model.Admin;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AdminDao extends CrudRepository<Admin, Integer> {
    Admin findByUsernameAndPassword(String username, String password);

    @Query
            (
                    value = "SELECT * FROM admin WHERE username=:u password=:p",
                    nativeQuery = true
            )
    Admin controlloLogin(@Param("u") String username, @Param("p") String password);
}

