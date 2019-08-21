package io.pinect.azeron.server.service;

import io.pinect.azeron.server.domain.dto.ResponseStatus;
import io.pinect.azeron.server.domain.dto.in.SeenDto;
import io.pinect.azeron.server.domain.dto.out.SeenResponseDto;
import io.pinect.azeron.server.domain.entity.MessageEntity;
import io.pinect.azeron.server.domain.repository.MessageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
@Log4j2
public class SeenService {
    private final MessageRepository messageRepository;
    private final Executor azeronExecutor;

    @Autowired
    public SeenService(MessageRepository messageRepository, Executor azeronExecutor) {
        this.messageRepository = messageRepository;
        this.azeronExecutor = azeronExecutor;
    }

    public SeenResponseDto seen(SeenDto seenDto){
        log.info("Seen received -> "+ seenDto.toString());
        if(seenDto.getMessageId() != null) {
            messageRepository.seenMessage(seenDto.getMessageId(), seenDto.getServiceName());
            removeMessageIdNeeded(seenDto.getMessageId());
        } else
            messageRepository.seenMessages(seenDto.getMessageIds(), seenDto.getServiceName());

        return new SeenResponseDto(ResponseStatus.OK, seenDto.getReqId());
    }

    private void removeMessageIdNeeded(String messageId) {
        azeronExecutor.execute(new Runnable() {
            @Override
            public void run() {
                MessageEntity messageEntity = messageRepository.getMessage(messageId);
                if(messageEntity.getSeenNeeded() == messageEntity.getSeenCount() || messageEntity.getSubscribers().size() == messageEntity.getSeenSubscribers().size())
                    messageRepository.removeMessage(messageEntity.getMessageId());
            }
        });
    }

}
