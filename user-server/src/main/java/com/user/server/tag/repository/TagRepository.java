package com.user.server.tag.repository;

import com.user.server.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagName(String tagName);

    @Query("SELECT t.tagName, COUNT(ptm) FROM ProductTagMapping ptm " +
            "JOIN ptm.tag t GROUP BY t.tagName ORDER BY COUNT(ptm) DESC")
    List<Object[]> findPopularTags(Pageable pageable);
}
