package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.hw.models.Sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SequenceServiceTest {

    @Mock
    private ObjectProvider<MongoTemplate> mongoTemplateProvider;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private SequenceService sequenceService;

    @Test
    void shouldReturnIncrementedSequenceValue() {
        when(mongoTemplateProvider.getObject()).thenReturn(mongoTemplate);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(Sequence.class)))
                .thenReturn(new Sequence("books", 4));
        assertThat(sequenceService.nextId("books")).isEqualTo("4");
    }

    @Test
    void shouldIncrementRequestedSequenceAtomically() {
        when(mongoTemplateProvider.getObject()).thenReturn(mongoTemplate);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(Sequence.class)))
                .thenReturn(new Sequence("comments", 1));

        sequenceService.nextId("comments");

        var query = ArgumentCaptor.forClass(Query.class);
        var update = ArgumentCaptor.forClass(Update.class);
        var options = ArgumentCaptor.forClass(FindAndModifyOptions.class);
        verify(mongoTemplate).findAndModify(query.capture(), update.capture(), options.capture(), eq(Sequence.class));
        assertThat(query.getValue().getQueryObject()).containsEntry("_id", "comments");
        assertThat(update.getValue().getUpdateObject()).containsKey("$inc");
        assertThat(options.getValue().isUpsert()).isTrue();
        assertThat(options.getValue().isReturnNew()).isTrue();
    }
}
