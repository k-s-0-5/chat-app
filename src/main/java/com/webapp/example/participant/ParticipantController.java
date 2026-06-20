package com.webapp.example.participant;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

  private final ParticipantRepository participantRepository;

  public ParticipantController(ParticipantRepository participantRepository) {
    this.participantRepository = participantRepository;
  }
}
