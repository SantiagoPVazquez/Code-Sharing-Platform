package platform.Model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.Model.Code;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends CrudRepository<Code, String> {
    Optional<Code> findById(String id);
    List<Code> findAllByRestrictTimeAndRestrictView(boolean restrictTime, boolean restrictViews);

}
