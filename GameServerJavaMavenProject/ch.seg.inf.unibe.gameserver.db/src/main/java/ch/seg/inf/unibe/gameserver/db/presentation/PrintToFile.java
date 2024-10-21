package ch.seg.inf.unibe.gameserver.db.presentation;

import ch.seg.inf.unibe.gameserver.db.logic.model.GameServer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class PrintToFile {

    public void print(GameServer gameServer, Path filePath) {
        String dataTree = gameServer.toString();

        try (FileWriter fileWriter = new FileWriter(filePath.toAbsolutePath().toString())) {
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(dataTree);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("GameServer data has been written to file: " + filePath.toAbsolutePath());
    }

}
