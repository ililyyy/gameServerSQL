package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends IdentifiableElement {

    private GameDescription isOfType;

    private List<Player> players;
    
    private List<Chat> chatOfSpectators;

    private Instant startTime;

    private Instant endTime;
    
    private Player winner;


    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> chatOfSpectators) {
        this.players = chatOfSpectators;
    }
    
    public List<Chat> getChatOfSpectators() {
        return chatOfSpectators;
    }

    public void setChatOfSpectators(List<Chat> chatOfSpectators) {
        this.chatOfSpectators = chatOfSpectators;
    }
    

    public GameDescription getIsOfType() {
        return isOfType;
    }

    public void setIsOfType(GameDescription isOfType) {
        this.isOfType = isOfType;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime.truncatedTo(ChronoUnit.SECONDS);
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime.truncatedTo(ChronoUnit.SECONDS);
    }
    
    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) +  "{" + "\n" +
                " ".repeat(indent) + "startTime=" + startTime + ",\n" +
                " ".repeat(indent) + "endTime=" + endTime + ",\n" +
                " ".repeat(indent) + "description=" + ref(isOfType) + ",\n" +
                " ".repeat(indent) + "winner=" + ref(winner) + ",\n" +
                " ".repeat(indent) + "players=" + refs(players) + ",\n" +
                " ".repeat(indent) + "chatOfSpectators=" + subs(chatOfSpectators, indent) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static Game createExampleData(List<GameDescription> gamesDescriptions, Tournament  tournament) {
        Game game = new Game();
        Random random = new Random();
        game.isOfType = gamesDescriptions.get(random.nextInt(gamesDescriptions.size() - 1) );

        game.players = new ArrayList<>();
        int playerA = random.nextInt(tournament.getParticipants().size() - 1);
        int playerB = (playerA + 1) % tournament.getParticipants().size();
        game.players.add(tournament.getParticipants().get(playerA));
        game.players.add(tournament.getParticipants().get(playerB));

        if (random.nextBoolean()) {
            game.setWinner(game.players.get(random.nextInt(1)));
        }

        game.chatOfSpectators = new ArrayList<>();
        game.chatOfSpectators.add(Chat.createExampleData(game));

        game.setStartTime(Instant.now().plusSeconds(random.nextInt(1000)));
        game.setEndTime(Instant.now().plusSeconds(random.nextInt(1000)));

        return game;
    }

    public void modifyExampleData(Tournament  tournament) {
        Random random = new Random();

        players = new ArrayList<>();
        int playerA = random.nextInt(tournament.getParticipants().size() - 1);
        int playerB = (playerA + 1) % tournament.getParticipants().size();
        players.add(tournament.getParticipants().get(playerA));
        players.add(tournament.getParticipants().get(playerB));

        if (random.nextBoolean()) {
            setWinner(players.get(random.nextInt(1)));
        } else {
            setWinner(null);
        }

        chatOfSpectators.forEach(c -> c.modifyExampleData(this));

        setStartTime(Instant.now().plusSeconds(random.nextInt(1000)));
        setEndTime(Instant.now().plusSeconds(random.nextInt(1000)));
    }
}
