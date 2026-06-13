package com.webapp.example.participant;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.example.account.Account;


@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantRepository participantRepository;

    public ParticipantController(ParticipantRepository participantRepository){
        this.participantRepository = participantRepository;
    }

    @GetMapping("")
    public List<Participant> findAllWithAccount(Account account){
        return participantRepository.findAllWithAccount(account);
    }
}
