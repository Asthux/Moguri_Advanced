package org.moguri.event.roulette.service;

import org.moguri.event.roulette.domain.Roulette;

public interface RouletteService {

    boolean hasPlayedRouletteToday(long memberId);

    void createRoulette(Roulette roulette);
}
