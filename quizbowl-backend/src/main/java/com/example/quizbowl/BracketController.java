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
@RequestMapping("/api/bracket")
@CrossOrigin(origins = "http://localhost:3000")
public class BracketController {

    private final BracketService bracketService;
    private final AuthService authService;

    public BracketController(BracketService bracketService, AuthService authService) {
        this.bracketService = bracketService;
        this.authService = authService;
    }

    @GetMapping
    public BracketState getState() {
        return bracketService.getState();
    }

    @PostMapping("/init")
    public void initBracket(@RequestBody InitBracketRequest request,
                            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        bracketService.initBracket(request.getTeamNames());
    }

    @PostMapping("/reset")
    public void resetBracket(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        bracketService.resetBracket();
    }

    @PostMapping("/set-current")
    public void setCurrentMatch(@RequestParam(name = "gameId", defaultValue = "default") String gameId,
                                @RequestBody SetCurrentMatchRequest request,
                                @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        bracketService.setCurrentMatch(request.getTeamAId(), request.getTeamBId(), gameId);
    }

    @PostMapping("/finalize-current")
    public void finalizeCurrentMatch(@RequestParam(name = "gameId", defaultValue = "default") String gameId,
                                     @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        authService.requireAdmin(token);
        bracketService.finalizeCurrentMatch(gameId);
    }
}
