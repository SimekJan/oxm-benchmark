package cz.cuni.mff.simekjan.application.query;

import cz.cuni.mff.simekjan.api.QueryApi;
import cz.cuni.mff.simekjan.model.QueryDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class QueryController implements QueryApi {

    private final QueryService queryService;

    @Override
    public ResponseEntity<QueryDetail> getQuery(UUID queryId) {
        try {
            return ResponseEntity.ok(queryService.getQuery(queryId));
        } catch (QueryService.QueryNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}
