package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Sequence;

/**
 * Позволяет генерировать id для коллекций в определенном порядке
 */
@RequiredArgsConstructor
@Service
public class SequenceService {

    private final ObjectProvider<MongoTemplate> mongoTemplateProvider;

    public String nextId(String sequenceName) {
        var mongoTemplate = mongoTemplateProvider.getObject();
        var sequence = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is(sequenceName)),
                new Update().inc("value", 1),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Sequence.class);
        return Long.toString(sequence.getValue());
    }
}
