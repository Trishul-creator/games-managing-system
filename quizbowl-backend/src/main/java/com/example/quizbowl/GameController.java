package com.example.quizbowl;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:3000")
public class GameController {

    private final GameService gameService;
    private final AuthService authService;

    public GameController(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    @GetMapping
    public GameState getState(@RequestParam(name = "gameId", defaultValue = "default") String gameId) {
        return gameService.getState(gameId);
    }

    @PostMapping("/team-names")
    public void setTeamNames(@RequestParam(name = "gameId", defaultValue = "default") String gameId,
                             @RequestBody TeamNamesRequest request,
                             @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        gameService.setTeamNames(gameId, request.getTeamAName(), request.getTeamBName());
    }

    @PostMapping("/reset")
    public void resetGame(@RequestParam(name = "gameId", defaultValue = "default") String gameId,
                          @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        gameService.resetGame(gameId);
    }

    @PostMapping("/next-tossup")
    public void nextTossup(@RequestParam(name = "gameId", defaultValue = "default") String gameId,
                           @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        gameService.nextTossup(gameId);
    }

    @PostMapping("/award-tossup")
    public void awardTossup(@RequestParam(name = "gameId", defaultValue = "default") String gameId,
                            @RequestBody AwardTossupRequest request,
                            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        gameService.awardTossup(gameId, request.getTeam());
    }

    @PostMapping("/award-bonus")
    public void awardBonus(@RequestParam(name = "gameId", defaultValue = "default") String gameId,
                           @RequestBody AwardBonusRequest request,
                           @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        gameService.awardBonus(gameId, request.getPoints());
    }
}
