package com.isy.game.player;
import com.isy.game.Board;
import com.isy.game.GameType;
import com.isy.game.ITile;
import com.isy.server.Server;
import com.isy.server.await.Promise;
import com.isy.util.GameCreator;

import static com.isy.server.ServerUtils.await;

public abstract class Player<T extends Enum<T> & ITile> {
    private final String name;
    private final T symbol;
    protected Server client;

    public Player(String name, T symbol, Server client){
        this.name = name;
        this.symbol = symbol;
        this.client = client;
    }

    public void setPlayerClient(Server client){
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public T getSymbol(){
        return symbol;
    }

    public abstract int[] getMove(Board<T> board);

    public void sendServerData(int[] move){
        await(new Promise().setCommand("move " + formatClientMove(move)));
    }

    /**
     * Formats move for game server's regular input
     * @param move int array with the x and y coords of the clicked tile.
     * @return a single int which was converted by calculating x + (y * 3)
     */
    public int formatClientMove(int[] move){
        int[] dimensions = GameType.getBoardDimensionsByGameType(GameCreator.getCurrentInstance().getGameType());
        return move[0] + (move[1] * dimensions[0]);
    }

    /**
     * Formats move from game server to regular input
     * @param move int of corresponding tile.
     * @return an int array including row and col.
     */
    public int[] formatServerMove(int move){
        int[] dimensions = GameType.getBoardDimensionsByGameType(GameCreator.getCurrentInstance().getGameType());
        int row = move % dimensions[0];
        int col = move / dimensions[0];
        return new int[]{row, col};
    }
}
