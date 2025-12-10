package profitsoft.intership.songsplaylist.dao;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import profitsoft.intership.songsplaylist.data.SongData;
import profitsoft.intership.songsplaylist.dto.song.SongInfoDto;
import profitsoft.intership.songsplaylist.dto.song.SongQueryDto;
import profitsoft.intership.songsplaylist.mapper.SongMapper;


import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class SongDao {

    private EntityManager entityManager;

    /**
     * Отримання списку SongInfoDto за запитом без пагінації
     * @param songQueryDto
     * @return
     */
    public List<SongInfoDto> getSongList(SongQueryDto songQueryDto) {
        return getSongList(songQueryDto, -1, -1);
    }

    /**
     * Отримання списку SongInfoDto за запитом із пагінацією
     * @param songQueryDto
     * @param page
     * @param size
     * @return
     */
    public List<SongInfoDto> getSongList(SongQueryDto songQueryDto, int page, int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SongData> cq = cb.createQuery(SongData.class);
        Root<SongData> root = cq.from(SongData.class);

        buildCriteriaQuery(songQueryDto, cb, cq, root);

        TypedQuery<SongData> query = entityManager.createQuery(cq);

        if(isForPagination(page, size)) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultList()
                .stream()
                .map(SongMapper::toInfoDto)
                .toList();
    }

    /**
     * Отримання кількості сторінок
     * @param songQueryDto
     * @return
     */
    public int getTotalPages(SongQueryDto songQueryDto) {
        int size = songQueryDto.getSize();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<SongData> countRoot = countQuery.from(SongData.class);

        buildCountQuery(songQueryDto, criteriaBuilder,
                countQuery, countRoot);
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        return (int) Math.ceil((double) totalElements / size);
    }

    /**
     * Перевірка для пагінації
     * @param page
     * @param size
     * @return
     */
    private boolean isForPagination(int page, int size) {
        return page >= 0 && size > 0;
    }

    /**
     * Створення запиту для списку SongInfoDto
     * @param songQueryDto
     * @param criteriaBuilder
     * @param criteriaQuery
     * @param root
     */
    private void buildCriteriaQuery(SongQueryDto songQueryDto, CriteriaBuilder criteriaBuilder,
                                    CriteriaQuery<SongData> criteriaQuery, Root<SongData> root) {

        List<Predicate> predicates = getPredicates(songQueryDto, criteriaBuilder, root);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
    }

    /**
     * Створення запиту для підрахунку кількості сторінок
     * @param songQueryDto
     * @param criteriaBuilder
     * @param countQuery
     * @param countRoot
     */
    private void buildCountQuery(SongQueryDto songQueryDto, CriteriaBuilder criteriaBuilder,
                                 CriteriaQuery<Long> countQuery, Root<SongData> countRoot) {

        List<Predicate> countPredicates = getPredicates(songQueryDto, criteriaBuilder, countRoot);
        countQuery.select(criteriaBuilder.count(countRoot))
                .where(countPredicates.toArray(new Predicate[0]));
    }

    /**
     * Отримання параметів для фільтрації
     * @param songQueryDto
     * @param criteriaBuilder
     * @param root
     * @return
     */
    private List<Predicate> getPredicates(SongQueryDto songQueryDto, CriteriaBuilder criteriaBuilder, Root<SongData> root){
        List<Predicate> predicates = new ArrayList<>();
        if (checkPlaylistId(songQueryDto.getPlaylistId())) {
            predicates.add(criteriaBuilder.equal(root.get("playlist").get("id"), songQueryDto.getPlaylistId()));
        }
        if (checkArtist(songQueryDto.getArtist())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("artist")), "%" + songQueryDto.getArtist().toLowerCase() + "%"));
        }
        if (checkYear(songQueryDto.getYear())) {
            predicates.add(criteriaBuilder.equal(root.get("year"), songQueryDto.getYear()));
        }
        return predicates;
    }


    private boolean checkPlaylistId(Long playlistId) {
        return playlistId != null;
    }

    private boolean checkArtist(String artist) {
        return artist != null && !artist.isBlank();
    }

    private boolean checkYear(Integer year) {
        return year != null;
    }
}
