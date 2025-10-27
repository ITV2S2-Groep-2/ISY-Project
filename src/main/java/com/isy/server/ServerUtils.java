package com.isy.server;

import com.isy.game.Game;
import com.isy.game.GameType;
import com.isy.game.Player;
import com.isy.game.PlayerType;
import com.isy.game.ticTacToe.AiPlayer;
import com.isy.game.ticTacToe.HumanPlayer;
import com.isy.game.ticTacToe.RemotePlayer;
import com.isy.game.ticTacToe.Tile;
import com.isy.gui.scene.GameScene;
import com.isy.server.await.Promise;

import javax.swing.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.isy.server.await.Await.asyncAwait;
import static com.isy.server.await.Await.await;

public class ServerUtils {
    public static Pattern playerToMovePattern = Pattern.compile("PLAYERTOMOVE:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
}
