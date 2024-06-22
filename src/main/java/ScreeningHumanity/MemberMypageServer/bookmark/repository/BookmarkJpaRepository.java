package ScreeningHumanity.MemberMypageServer.bookmark.repository;

import ScreeningHumanity.MemberMypageServer.bookmark.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkJpaRepository extends JpaRepository<BookmarkEntity, Long> {

    Boolean existsByUuidAndStockCode(String uuid, String stockCode);
}