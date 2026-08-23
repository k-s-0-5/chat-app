package com.webapp.example.participant;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for participants, currently unused */
@RestController
@RequestMapping("/participants")
public class ParticipantController {

  @SuppressWarnings("unused")
  private final ParticipantRepository participantRepository;

  public ParticipantController(ParticipantRepository participantRepository) {
    this.participantRepository = participantRepository;
  }
}
