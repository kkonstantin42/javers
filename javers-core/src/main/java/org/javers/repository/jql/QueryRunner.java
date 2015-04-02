package org.javers.repository.jql;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.List;

/**
 * Adapter from a JqlQuery to JaversRepository API
 *
 * Created by bartosz.walacik on 2015-03-29.
 */
public class QueryRunner {
    private final JaversExtendedRepository repository;
    private final GlobalIdFactory globalIdFactory;

    public QueryRunner(JaversExtendedRepository repository, GlobalIdFactory globalIdFactory) {
        this.repository = repository;
        this.globalIdFactory = globalIdFactory;
    }

    public Optional<CdoSnapshot> runQueryForLatestSnapshot(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        return repository.getLatest(fromDto(globalId));
    }

    public List<CdoSnapshot> queryForSnapshots(JqlQuery query){
        Validate.argumentIsNotNull(query);

        if (query.isIdOnlyQuery()){
            return repository.getStateHistory(fromDto(query.getIdFilter()), query.getLimit());
        }

        if (query.isPropertyQuery()){
            return repository.getPropertyStateHistory(fromDto(query.getIdFilter()), query.getPropertyName(), query.getLimit());
        }

        throw new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "JqlQuery " + query + " is not supported");
    }

    public List<Change> queryForChanges(JqlQuery query){
        Validate.argumentIsNotNull(query);

        if (query.isIdOnlyQuery()){
            return repository.getChangeHistory(fromDto(query.getIdFilter()), query.getLimit());
        }

        if (query.isPropertyQuery()){
            return repository.getPropertyChangeHistory(fromDto(query.getIdFilter()), query.getPropertyName(), query.getLimit());
        }

        throw new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "JqlQuery " + query + " is not supported");
    }

    private GlobalId fromDto(GlobalIdDTO globalIdDTO) {
        return globalIdFactory.createFromDto(globalIdDTO);
    }
}