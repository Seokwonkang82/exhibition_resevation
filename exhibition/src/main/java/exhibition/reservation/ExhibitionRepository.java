package exhibition.reservation;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="exhibitions", path="exhibitions")
public interface ExhibitionRepository extends PagingAndSortingRepository<Exhibition, Long>{


}
